function removeSaccades(input_dir, type, type2)

if ~exist('input_dir', 'var')
    fprintf('Need an input_dir\n');
    return
end

output_dir=fullfile(input_dir);
input_dir=fullfile(input_dir,'data');

if ~exist(input_dir, 'dir')
    fprintf('intput_dir does''nt exist\n');
    return
end


if ~exist('type', 'var')
    type=0;
end

if ~exist('type2', 'var')
    type2=0;
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


filenames=dir([input_dir, '/capture_img_out_*.ppm']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/capture_img_out_*.jpg']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/capture_img_out_*.png']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/capture_img_out_*.bmp']);
end

N=length(filenames);

logs=read_log_file([input_dir '/image_save/Log_data.txt']);
timestamps=logs.Data(:,14);
fixs=logs.Data(:,8:9)+ones(size(logs.Data,1),1)*logs.Offset_xy;
if type==1
    fixations=dispersionExtraction(max(fixs,-200));
else
    fixations=HMMExtraction(max(fixs,-200));
end;
disp(fixations);

unix(['rm ' output_dir '/*.jpg']);
unix(['rm ' imgFixs_dir '/*']);

names=zeros(1,size(filenames(1).name,2));
names2=zeros(1,size(filenames(1).name,2));
eye_pos=zeros(1,2);
if type2==0
    n=0;
    for i=1:size(fixations,1)
        k=fixations(i,1);
        if fixations(i,3)<=0 || fixations(i,2)<=0 || fixations(i,3)>logs.siz_Outimg(1) || fixations(i,2)>logs.siz_Outimg(2)
            fprintf('Dropping out-of-range fixation points...\n');
            if fixations(i,3)<=0
                fprintf('\thigh\n');
            end
            if fixations(i,2)<=0
                fprintf('\tleft\n');
            end
            if fixations(i,3)>logs.siz_Outimg(1)
                fprintf('\tbottom\n');
            end
            if fixations(i,2)>logs.siz_Outimg(2)
                fprintf('\tright\n');
            end
            continue
        end;
        n=n+1;
        bestScore=0;
        argMax=-1;
        for k=round(fixations(i,1)-30/3*fixations(i,4)):round(fixations(i,1)+30/3*fixations(i,4))
            input_name=fullfile(input_dir,filenames(k).name);
            img=imread(input_name);
            tmp=sharpnessScore(img);
            if tmp>bestScore
                argMax=k;
                bestScore=tmp;
            end
    %         if isVerticalSync(img) || k==1
    %             break;
    %         else
    %             k=k-1;
    %             fprintf('Dropping out-of-VSync frame : %i\n',k);
    %         end;
        end;
        input_name=fullfile(input_dir,filenames(argMax).name);
        output_name=fullfile(output_dir, filenames(argMax).name);
        names(n,:)=filenames(argMax).name;
        names2(n,:)=filenames(argMax+1).name;
        eye_pos(n,:)=round([fixations(i,3) fixations(i,2)]);
        copyfile(input_name,output_name);
        img=imread(input_name);
        img=imposelabel(img,eye_pos(n,:));
        imwrite(img,fullfile(imgFixs_dir,filenames(k).name));
    end
else
    n=0;
    for i=1:size(fixations,1)
        for k=fixations(i,5):fixations(i,6)
            if fixs(k,1)<=0 || fixs(k,2)<=0 || fixs(k,2)>logs.siz_Outimg(1) || fixs(k,1)>logs.siz_Outimg(2)
                fprintf('Dropping out-of-range fixation points...\n');
                if fixs(k,2)<=0
                    fprintf('\thigh\n');
                end
                if fixs(k,1)<=0
                    fprintf('\tleft\n');
                end
                if fixs(k,2)>logs.siz_Outimg(1)
                    fprintf('\tbottom\n');
                end
                if fixs(k,1)>logs.siz_Outimg(2)
                    fprintf('\tright\n');
                end
                continue
            end;
            n=n+1;
            input_name=fullfile(input_dir,filenames(k).name);
            output_name=fullfile(output_dir, filenames(k).name);
            names(n,:)=filenames(k).name;
            names2(n,:)=filenames(k+1).name;
            eye_pos(n,:)=round([fixs(i,2) fixs(i,1)]);
            copyfile(input_name,output_name);
            %img=imread(input_name);
            %img=imposelabel(img,eye_pos(n,:));
            %imwrite(img,fullfile(imgFixs_dir,filenames(k).name));
        end
    end
end
% fid = fopen(fullfile(output_dir,'eye_positions.txt'),'w');
% fprintf(fid,'%f %f\n',( fixations(:,2:3) )');
% fclose(fid);
names=char(names);
names2=char(names2);
save([output_dir '/save.mat'],'eye_pos','names','names2');
end

function result = sharpnessScore(img)
    tmp=im2double(rgb2gray(img));
    Gx=imfilter(imfilter(tmp,[-1 0 1]),[1;2;1]);
    Gy=imfilter(imfilter(tmp,[1 2 1]),[1;0;-1]);
    gradient=sqrt(Gx.^2+Gy.^2);
    result=norm([mean(gradient(:)) std(gradient(:))]); 
end

function result = isVerticalSync(img)
tmp=im2double(img);
tmp=tmp(2:size(tmp,1),:)-tmp(1:size(tmp,1)-1,:);
tmp=tmp(2:size(tmp,1),:)-tmp(1:size(tmp,1)-1,:); % second degree derivative
result = max(sum(tmp,2)/size(tmp,2)) < 0.07; 
end

% Keep relevant fixation points from raw data
% fixs : array of [x y] for each image
% timestamps : vector of images' timestamps
% result : matrix of found fixation points
%       first column -> index of image
%       second/third column -> x/y
%       fourth column -> duration of fixation
function result = dispersionExtraction(fixs,timestamps,durationThreshold,dispersionThreshold)

if ~exist('durationThreshold', 'var') || isempty(durationThreshold)
    durationThreshold=0.2;
end

if ~exist('dispersionThreshold', 'var') || isempty(dispersionThreshold)
    dispersionThreshold=150;
end

N=size(fixs,1);

if ~exist('timestamps', 'var') || isempty(timestamps)
    timestamps = 1/30*(1:N)';
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
       d=timestamps(j)-timestamps(i);
       n=n+1;
       sumPt=sumPt+fixs(j,:);
       minPt=min(minPt,fixs(j,:));
       maxPt=max(maxPt,fixs(j,:));
       j=j+1;
   end
   if d<=durationThreshold % whitout it the last image would always be considered as a fixation point
       break
   end
   if sum(maxPt-minPt)<=dispersionThreshold && j-i>=2 %this is a fixation point
       while j<=N && sum(max(maxPt,fixs(j,:))-min(minPt,fixs(j,:)))<=dispersionThreshold %add point while the dispersion is not too big
           d=timestamps(j)-timestamps(i);
           n=n+1;
           sumPt=sumPt+fixs(j,:);
           minPt=min(minPt,fixs(j,:));
           maxPt=max(maxPt,fixs(j,:));
           j=j+1;
       end
       numberFixations=numberFixations+1;
       result(numberFixations,:)=[round(i+n/2) sumPt/n d i j-1];
       i=j;
   else
       i=i+1;
   end
end

fprintf('%i fixations points found \n',numberFixations);
end

% Extraction based on Viterbi algorithm on Hidden Markov Models
function result = HMMExtraction(fixs,timestamps)
addpath(genpath('../HMMall/'));
N=size(fixs,1);

if ~exist('timestamps', 'var') || isempty(timestamps)
    timestamps = 1/30*(1:N)';
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
        d=timestamps(j-1)-timestamps(i);
        if d>=0.1 && j-i>=2
            numberFixations=numberFixations+1;
            result(numberFixations,:)=[round((i+j-1)/2) sum(fixs(i:j-1,:),1)/(j-i) d i j-1];
        end
        i=j;
	else
		i=i+1;	
	end
end
fprintf('%i fixations points found \n',numberFixations);
end
