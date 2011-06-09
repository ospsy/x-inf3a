function seg(input_dir, flow,sobel, starti, endi)
% Demo for single image only
    % add paths
% addpath(genpath('../../../shared'));
% addpath(genpath('../Recog'));
% addpath( fullfile(pwd,'/edgeDetector/'));
% addpath( fullfile(pwd,'/mexSegFixatedRegion/Release'));

%addpath( fullfile(pwd,'/activeSegmentation'));
%addpath( fullfile(pwd,'/mexSegFixatedRegion/Debug'));

if ~exist('input_dir', 'var')
    input_dir = '../mydata/rsPNGV2';
    input_dir = '/media/SEA_DISC/ObjRecog/mydata/AmazonFlickr/AppleiPodclassic/00001';
    input_dir = '/media/SEA_DISC/data/egocentric_objects_intel_06_2009/keyframes';    
    input_dir = '/media/SEA_DISC/ObjRecog/mydata/AmazonFlickr_ALL/AppleMagicMouse/00001';

    input_dir = '/media/SEA_DISC/data/AlphaMattingDataset/all/';
    
    input_dir = '/media/SEA_DISC/data/GrabAlpha/composite_500';
    
    input_dir = '/media/SEA_DISC/data/WillowGarage/all';
end

if ~exist('randomly', 'var')
    randomly = 1;    
end

if ~exist('sobel', 'var')
    sobel = 0;    
end

if ~exist('flow', 'var')
    flow = 1;    
end


output_dir=[input_dir, '/output'];
if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

fix_dir=[output_dir, '/fix'];
if(~exist(fix_dir, 'dir'))
    mkdir(fix_dir);
end

if flow 
    if sobel
        cluster_dir=[output_dir, '/segs_sobel_OF/'];
    else
        cluster_dir=[output_dir, '/segs_OF/'];
    end
else 
    if sobel
        cluster_dir=[output_dir, '/segs_sobel/'];
    else
        cluster_dir=[output_dir, '/segs/'];
    end
end
if(~exist(cluster_dir, 'dir'))
    mkdir(cluster_dir);
end

dis=0;

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
    
    [pathstr, name, ext] = fileparts(imgFileName);

    if 0
        figure(100); 
        set(gcf,'NAME','segmentation process(monocular)');
    end

    % test image
    img  = imread(imgFileName);
    if dis
        figure(100); subplot(2,3,1);
        imshow(img); axis image;
        title(' Original image');
    end
    segment_bin='./segment'; 
    fix_txt=fullfile(fix_dir, [name, '_fix.txt']);    
    
    if 1
        if ~exist(tmp_dir, 'dir')
            mkdir(tmp_dir);
            disp(['tmp_dir:', tmp_dir]);
        else
            delete([tmp_dir, '/*.*']);
        end 

        cmd=['export LD_LIBRARY_PATH=""; ', segment_bin, ' -i ', imgFileName, ' -o ', tmp_dir, ' -f ',fix_txt];
        if sobel
             cmd=[cmd ' -sobel ']
        end
        if flow
            floname=fullfile(input_dir,fullfile('flow',[name '.png']));
            if exist(floname,'file')
                cmd=[cmd ' -flow ' floname ]
            else
                disp('No optical flow file...')
            end 
        end
        unix(cmd);
    end
    
    fixs=load(fix_txt);
    fixs=[fixs(:,2), fixs(:,1)];
    
    cnt = 0;
    
    for j=1:size(fixs,1)
        segfile=fullfile(tmp_dir, sprintf('_region_%d.png', j));
        if exist(segfile, 'file')
            fgMapWtColor=imread(segfile);
            fgMapWtColor=preprocessRegion(fgMapWtColor>250);
            fgMapWtColor=logical(fgMapWtColor)*255;
            bd = bwboundaries(fgMapWtColor > 250, 8, 'noholes');
            if length(bd)>=1
                cnt = cnt +1;
                img_fg = img;
                if 1
                    for bdi=1:length(bd)
                        img_fg = imposelabel(img_fg, bd{bdi});
                    end
                    %img_fg = imposelabel(img_fg, fixs(j,:), 11, [255, 0, 0]);
                end
                if dis
                    imshow(img_fg); hold on;
                    axis image;
                    hold off;
                    title('Enclosing contour (Without Color)');
                end
                fg_name=[name, sprintf('_%03d_fg2.jpg', cnt)];
                imwrite(img_fg, fullfile(tmp_dir, fg_name));

                ii=fgMapWtColor==0;
                tmp=img_fg(:,:,1);
                tmp(ii)=255;
                img_fg(:,:,1)=tmp;
                tmp=img_fg(:,:,2);
                tmp(ii)=255;
                img_fg(:,:,2)=tmp;
                tmp=img_fg(:,:,3);
                tmp(ii)=255;
                img_fg(:,:,3)=tmp;  
                wtclr_name=[name, sprintf('_%03d_wtclr.jpg', cnt)];
                imwrite(img_fg, fullfile(tmp_dir, wtclr_name));                
                res_mat = [name, sprintf('_%03d_res.mat', cnt)];
                save(fullfile(tmp_dir, res_mat), 'fgMapWtColor');
                
            end
        end
    end     
    disp('Clustering');    
    img_dir=pathstr;
    segmentClustering(name, tmp_dir, cluster_dir, img_dir,fixs);
end






