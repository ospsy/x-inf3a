function removeSaccades(input_dir, output_dir, type)

if ~exist('input_dir', 'var')
    fprintf('Need an input_dir\n');
    return
end

if ~exist('output_dir', 'var')
    fprintf('Need an output_dir\n');
    return
end

if ~exist('type', 'var')
    type=1;
end

if ~exist(input_dir, 'dir')
    fprintf('intput_dir does''nt exist\n');
    return
end

if ~exist(output_dir, 'dir')
    mkdir(output_dir);
end

imgFixs_dir=[output_dir '/output/imgs_fixs'];
if ~exist(imgFixs_dir, 'dir')
    mkdir(imgFixs_dir);
end


filenames=dir([input_dir, '/*.ppm']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.jpg']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.bmp']);
end

N=length(filenames);

logs=read_log_file(fullfile(input_dir,'Log_data.txt'));
if type==1
    fixations=dispersionExtraction(max(logs.Data(:,8:9),-200));
else
    fixations=HMMExtraction(max(logs.Data(:,8:9),-200));
end;
disp(fixations);

unix(['rm ' output_dir '/*']);
unix(['rm ' imgFixs_dir '/*']);

for i=1:size(fixations,1)
    k=fixations(i,1);
    while 1
        input_name=fullfile(input_dir,filenames(k).name);
        output_name=fullfile(output_dir, filenames(k).name);
        img=imread(input_name);
        if isVerticalSync(img) || k==1
            break;
        else
            k=k-1;
            fprintf('Dropping out-of-VSync frame : %i',k);
        end;
    end;
    copyfile(input_name,output_name);
    img=imposelabel(img,round([fixations(i,3) fixations(i,2)]));
    imwrite(img,fullfile(imgFixs_dir,filenames(k).name));
end
fid = fopen(fullfile(output_dir,'eye_positions.txt'),'w');
fprintf(fid,'%f %f\n',( fixations(:,2:3) )');
fclose(fid);

end

function result = isVerticalSync(img)
tmp=im2double(img);
tmp=tmp(2:size(tmp,1),:)-tmp(1:size(tmp,1)-1,:);
tmp=tmp(2:size(tmp,1),:)-tmp(1:size(tmp,1)-1,:); % second degree derivative
result = max(sum(tmp,2)/size(tmp,2)) < 0.07; 
end

% Keep relevant fixation points from raw data
% fixs : array of [x y] for each image
% durations : vector of images durations
% result : matrix of found fixation points
%       first column -> index of image
%       second/third column -> x/y
%       fourth column -> duration of fixation
function result = dispersionExtraction(fixs,durations,durationThreshold,dispersionThreshold)

if ~exist('durationThreshold', 'var') || isempty(durationThreshold)
    durationThreshold=0.2;
end

if ~exist('dispersionThreshold', 'var') || isempty(dispersionThreshold)
    dispersionThreshold=150;
end

N=size(fixs,1);

if ~exist('durations', 'var') || isempty(durations)
    durations = 1/25*ones(N,1);
end

i=1;%origin of considered window
numberFixations=0;
result=[ ];

while i<=N
   d=0;%duration of window
   n=0;%# of points in window
   sumPt=[0 0];
   minPt=[1000000 1000000];
   maxPt=[0 0];
   j=i;
   while j<=N && d<=durationThreshold %initialisation on a minimal duration window
       d=d+durations(j);
       n=n+1;
       sumPt=sumPt+fixs(j,:);
       minPt=min(minPt,fixs(j,:));
       maxPt=max(maxPt,fixs(j,:));
       j=j+1;
   end
   if d<=durationThreshold % whitout it the last image would always be considered as a fixation point
       break
   end
   if sum(maxPt-minPt)<=dispersionThreshold %this is a fixation point
       while j<=N && sum(max(maxPt,fixs(j,:))-min(minPt,fixs(j,:)))<=dispersionThreshold %add point while the dispersion is not too big
           d=d+durations(j);
           n=n+1;
           sumPt=sumPt+fixs(j,:);
           minPt=min(minPt,fixs(j,:));
           maxPt=max(maxPt,fixs(j,:));
           j=j+1;
       end
       numberFixations=numberFixations+1;
       result(numberFixations,:)=[round(i+n/2) sumPt/n d];
       i=j;
   else
       i=i+1;
   end
end

fprintf('%i fixations points found \n',numberFixations);
end

% Extraction based on Viterbi algorithm on Hidden Markov Models
% 	A : transition's probabilities matrix
%	m : Gaussian means vector
%	sigma : Gaussian variances vector
function result = HMMExtraction(fixs,durations)
addpath(genpath('../HMMall/'));
N=size(fixs,1);

if ~exist('durations', 'var') || isempty(durations)
    durations = 1/25*ones(N,1);
end

velocities=fixs(2:N,:)-fixs(1:N-1,:);
velocities=sum(velocities.^2,2).^0.5; % norm-2 of the velocities
velocities=velocities';

m=[50 300];
m=reshape(m, [1 2 1]);
sigma=[50;5000];
sigma=reshape(sigma, [1 1 2 1]);
transmat=[0.95 0.05 ; 0.05 0.95];
prior=[0.5 ; 0.5];

[LL, prior, transmat, m, sigma, ] = mhmm_em(velocities, prior, transmat, m, sigma, [] , 'max_iter', 5);

B= mixgauss_prob(velocities,m,sigma);
path=viterbi_path(prior, transmat, B);
disp(path');
sigma
m
transmat
i=1;
numberFixations=0;
while i<=size(path,2)
	if path(i)==1
		j=i;
        while j<=size(path,2) && path(j)==1
			j=j+1;
        end
        d=sum(durations(i:j-1,:));
        if d>=0.15
            numberFixations=numberFixations+1;
            result(numberFixations,:)=[round((i+j-1)/2) sum(fixs(i:j-1,:),1)/(j-i) d];
        end
        i=j;
	else
		i=i+1;	
	end
end
fprintf('%i fixations points found \n',numberFixations);
end
