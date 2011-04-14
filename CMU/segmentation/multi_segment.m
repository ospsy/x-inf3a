function multi_segment(input_dir, starti, endi)
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

output_dir=[input_dir, '/output'];
if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

seg_dir=[output_dir, '/segs'];
if(~exist(seg_dir, 'dir'))
    mkdir(seg_dir);
end

select_dir=[output_dir, '/segs/select/'];
if(~exist(select_dir, 'dir'))
    mkdir(select_dir);
end

multi_dir=[output_dir, '/segs/multi/'];
if(~exist(multi_dir, 'dir'))
    mkdir(multi_dir);
end

filenames=dir([input_dir, '/*.jpg']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.ppm']);
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

L=[200, 300, 400, 500, 600, 700, 800, 900, 1000];

minimum_segsize = 1600;
minimum_segsize = 100;
onboarder_threshold=0.1;

for i=starti:endi,    
    imgFileName=fullfile(input_dir, filenames(i).name);
        
    disp(imgFileName);
    img  = imread(imgFileName);

    [pathstr, name, ext, versn] = fileparts(imgFileName);   
    
    if ~exist(tmp_dir, 'dir')
        mkdir(tmp_dir);
        disp(['tmp_dir:', tmp_dir]);
    else
        delete([tmp_dir, '/*.*']);
    end 

    ppm=fullfile(tmp_dir, 'tmp.ppm');
    imwrite(img, ppm, 'ppm');

    out=fullfile(tmp_dir, 'tmp.txt');
    
    cnt = 0;
    for l=1:length(L)
        cmd=sprintf('!multisegment 0.5 %d 50 %s %s', L(l), ppm, out); %/home/hongwenk/workspace/multisegment/Release/

        eval(cmd);

        a=load(out);
        u=unique(a(:));
        seg=a;
        
        for j=1:length(u)
            b=double(a==u(j));
            seg(a==u(j))=j;
%             STATS = regionprops(b, {'Centroid','Solidity'});
%             p=round(STATS(1).Centroid);
            if true %STATS(1).Solidity>0.5&&p(1)>0&&p(1)<=size(img,2)&&p(2)>0&&p(2)<=size(img,1)&&b(p(2), p(1))>0                            
                fgMapWtColor=uint8(b*255);
                fgMapWtColor=preprocessRegion(fgMapWtColor>250);
                fgMapWtColor=logical(fgMapWtColor)*255;            
                bd = bwboundaries(fgMapWtColor > 250, 8, 'noholes');
                if length(bd)>=1                
                    tic;
                    imheight=size(fgMapWtColor, 1);
                    imwidth=size(fgMapWtColor, 2);
                    ratio=segOnboarderRatio(bd, imheight, imwidth);
                    s=sum(fgMapWtColor(:)>0);
                    
                    if s>minimum_segsize%&& ratio<onboarder_threshold           
                        cnt=cnt+1;
                        img_fg = img;
                        if 1
                            for bdi=1:length(bd)
                                img_fg = imposelabel(img_fg, bd{bdi});
                            end                    
                        end

                        fg_name=[name, sprintf('_%03d_fg2.jpg', cnt)];
                        imwrite(img_fg, fullfile(tmp_dir, fg_name));    

                        resfile=fullfile(tmp_dir, sprintf('%s_%03d_res.mat', name, cnt));
                        save(resfile, 'fgMapWtColor');            
                    end
                end

            end
        end    
    	segfile=fullfile(multi_dir, sprintf('%s_%05d_multi.mat',name,l));
    	save(segfile, 'seg');
    end       
    
    disp('Clustering');    
    img_dir=pathstr;
    segmentClustering(name, tmp_dir, select_dir, img_dir);
end
end

