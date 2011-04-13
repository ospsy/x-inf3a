function fixation(input_dir, starti, endi)
% Demo for single image only

% add paths
% addpath( fullfile(pwd,'/edgeDetector/'));
% addpath( fullfile(pwd,'/activeSegmentation'));
% addpath(genpath('../../shared'))

if ~exist('input_dir', 'var')
    input_dir = '../mydata/rsPNGV2';
    input_dir = '/media/SEA_DISC/ObjRecog/mydata/AmazonFlickr/AppleiPodclassic/00001';
    input_dir = '/media/SEA_DISC/data/egocentric_objects_intel_06_2009/keyframes';    
    input_dir = '/media/SEA_DISC/ObjRecog/mydata/AmazonFlickr_ALL/AppleMagicMouse/00001';
	input_dir = '/media/SEA_DISC/data/AlphaMattingDataset/all';
    input_dir = '/media/SEA_DISC/data/GrabAlpha/composite_500';
%     input_dir = '/media/SEA_DISC/data/WillowGarage/all';
end

if ~exist('randomly', 'var')
    randomly = 1;
end

if ~exist('nfix', 'var')
    nfix = 100;
end

level=4;
pyramid=pyramidSampling(level);    
sx=4^(level-1)*2;
sy=4^(level-1)*2;

output_dir=[input_dir, '/output'];
if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

fix_dir=[output_dir, '/fix'];
if(~exist(fix_dir, 'dir'))
    mkdir(fix_dir);
end

filenames=dir([input_dir, '/*.ppm']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.jpg']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end

N=length(filenames);

if ~exist('starti', 'var')
    starti = 1;    
elseif ischar(starti)
    starti = str2num(starti);
end

if ~exist('endi', 'var')
    endi = N;   
elseif ischar(endi)
    endi = str2num(endi);    
end

tmp_dir = [output_dir, '/tmp/'];
if ~exist(tmp_dir, 'dir')
    mkdir(tmp_dir);
end
created=false;
while ~created
    rand('twister', sum(100*clock));
    r=round(rand()*10000);
    tmp_dir = [output_dir, sprintf('/tmp/%05d/', r)];
    if ~exist(tmp_dir, 'dir')
        mkdir(tmp_dir);
        disp(['tmp_dir:', tmp_dir]);
        created=true;        
    end
end

for i=starti:endi,    
    imgFileName=fullfile(input_dir, filenames(i).name);
        
    disp(imgFileName);
    img  = imread(imgFileName);

    [pathstr, name, ext, versn] = fileparts(imgFileName);

    cnt = 0;

    fixPts = cell(0);

    if ~randomly
        while (1)
            cnt = cnt + 1;
            % select fixation point
            disp( 'select a fixation point');
            [fixPt, button] = selectFixPt(img);

            if button == 1 

                img_fix = imposelabel(img, fixPt, 11);
                fix_name=[name, sprintf('_%03d_fix.jpg', cnt)];
                imwrite(img_fix, fullfile(pathstr, '/output/', fix_name));

                fixPts = [fixPts, {fixPt}];

            else
                fix_mat = [name, '_fix.mat'];
                save(fullfile(pathstr, '/output/', fix_mat), 'fixPts');
                break;
            end
        end
    else
        rid=[];
        if 0
            npixel = size(img, 1)*size(img, 2);
            r=randperm(npixel);
            rid=r(1:min(nfix, npixel));        
        elseif 0            
            if size(img,2)>size(img,1)
                sigy=100;
                sigx=sigy*size(img,2)/size(img,1);
            else
                sigx=100;
                sigy=sigx*size(img,1)/size(img,2);
            end
            
            sigx=size(img,2);
            sigy=size(img,1);
            
            [x,y]=meshgrid(1:size(img,2), 1:size(img,1));
            xy=cat(3,x,y);
            
            xu=x-ones(size(img,1), size(img,2))*size(img,2)/2;
            yu=y-ones(size(img,1), size(img,2))*size(img,1)/2;
            
            g=1/(sigx*sigy*sqrt(2*pi))*exp(-(xu.^2/(2*sigx^2)+yu.^2/(2*sigy^2)));
            g=g/max(g(:));
            
            rv=rand(size(img,1), size(img,2));            
            rgv = rv.*g;            
            [m, r]=sort(rgv(:), 'descend');            
            npixel = size(img, 1)*size(img, 2);
            rid=r(1:min(10000, npixel));        
            ri=randperm(10000);
            rid=rid(ri(1:min(nfix, 10000)));        
        else                     
            rx=size(img, 2)/sx;
            ry=size(img, 1)/sy;
            pyramidr=round([pyramid(:,1)*rx, pyramid(:,2)*ry]);
            
            rid=sub2ind([size(img,1), size(img,2)], pyramidr(:,2), pyramidr(:,1));            
        end
        if 0
            figure(1);
            imshow(img);
        end
        [py, px]=ind2sub([size(img,1), size(img,2)], rid);
        
        if 0
            pt=multisegment(img, tmp_dir);
            py=[py;pt(:,1)];
            px=[px;pt(:,2)];
        end
        
        fixPt=[py,px];        
        inbound=logical(px>0&px<size(img,2)&py>0&py<size(img,1));
        fixPt=fixPt(inbound,:);
        fixPts = mat2cell(fixPt, ones(size(fixPt,1), 1),2);
        if 0
            hold on;
            plot(fixPt(:,1), fixPt(:,2), '*', 'MarkerSize', 12);
        end       
        fix_mat = [name, '_fix.mat'];
        save(fullfile(fix_dir, fix_mat), 'fixPts');
        fix_txt = [name, '_fix.txt'];
        dlmwrite(fullfile(fix_dir, fix_txt), [px,py], 'newline', 'pc', 'delimiter', ' ');
        pause(1);
    end
end
end

function pyramid=pyramidSampling(level)

    level0=[2,2;
            6,2;
            2,6;
            6,6;
            0,4;
            4,0];

    pyramid=level0;
    for i=1:level-1
        leveli=level0*2^i;    
        pyramid=[pyramid; leveli];
    end

    pyramid=[[pyramid(:,1)+4^(level-1), pyramid(:,2)];
             [-pyramid(:,1)+4^(level-1), pyramid(:,2)]];
    pyramid=[[pyramid(:,1), pyramid(:,2)+4^(level-1)];
            [pyramid(:,1), -pyramid(:,2)+4^(level-1)]];

    pyramid=[pyramid;
             [4^(level-1), 4^(level-1)]];
    if 0
        plot(pyramid(:,1), pyramid(:,2), '+', 'MarkerSize', 12, 'LineWidth', 3);
    end

end

function pt=multisegment(img, tmp_dir)

if ~exist(tmp_dir, 'dir')
    mkdir(tmp_dir);
    disp(['tmp_dir:', tmp_dir]);
else
    delete([tmp_dir, '/*.*']);
end 

ppm=fullfile(tmp_dir, 'tmp.ppm');
imwrite(img, ppm, 'ppm');

out=fullfile(tmp_dir, 'tmp.txt');

cmd=sprintf('!/home/hongwenk/workspace/multisegment/Release/multisegment 0.5 200 50 %s %s', ppm, out); %

eval(cmd);

a=load(out);
u=unique(a(:));

pt=[];

for i=1:length(u)
    b=double(a==u(i));
    STATS = regionprops(b, {'Centroid','Solidity'});
    p=round(STATS(1).Centroid);
    
    if STATS(1).Solidity>0.5&&p(1)>0&p(1)<=size(img,2)&&p(2)>0&p(2)<=size(img,1)&&b(p(2), p(1))>0
        pt=[pt; [p(2), p(1)]];
    end
end
end