
function segmentClustering(name, input_dir, output_dir, img_dir)

% addpath('../../shared');

% if ~exist('input_dir', 'var') || isempty(input_dir)
%     input_dir = '../mydata/resized/output/rand/';
% end
% 
% if ~exist('output_dir', 'var') || isempty(output_dir)
%     output_dir = '../mydata/resized/output/rand/cluster/';
% end
% 
% if ~exist('img_dir', 'var') || isempty(img_dir)
%     img_dir = '../mydata/resized/';
% end

disp(input_dir);
disp(output_dir);
disp(img_dir);
disp(name);

overlap_threshold = 0.9;
minimum_segsize = 1600;
onboarder_threshold=0.1;
resfiles=dir(sprintf('%s/%s_*_res.mat', input_dir, name));

disp(length(resfiles));

segs=cell(0);
for j=1:length(resfiles)    
    res_mat = fullfile(input_dir, resfiles(j).name);
    if exist(res_mat, 'file')
       load(res_mat, 'fgMapWtColor');
       % filter out small regions
       s=sum(fgMapWtColor(:)>0);
	disp(res_mat);	

       tic;
       bd = bwboundaries(fgMapWtColor > 0); 
       toc
       tic;
       imheight=size(fgMapWtColor, 1);
       imwidth=size(fgMapWtColor, 2);
       ratio=segOnboarderRatio(bd, imheight, imwidth);
       toc
        if 0
           tic;
           STATS = regionprops(double(logical(fgMapWtColor > 0)), {'Solidity'});       
           toc
        end

       if s>minimum_segsize&& ratio<onboarder_threshold % && STATS(1).Solidity>0.5
           segs = [segs, {fgMapWtColor}];
       end
    end
end
if length(segs)<2
    segs_cluster=segs;
else
    segs_cluster = cluster_segs(segs, overlap_threshold);
end
imgFileName = sprintf([img_dir,'/%s.ppm'], name);
if~exist(imgFileName, 'file')
    imgFileName = sprintf([img_dir, '/%s.jpg'], name);
    if~exist(imgFileName, 'file')
        imgFileName = sprintf([img_dir, '/%s.png'], name);
        if~exist(imgFileName, 'file')
            return;
        end
    end
end    
img = imread(imgFileName);
for j=1:length(segs_cluster)
    res_mat = fullfile(output_dir, sprintf('%s_%03d_res.mat', name, j));
    disp(res_mat);
    fgMapWtColor = uint8(segs_cluster{j});
    save(res_mat, 'fgMapWtColor');

    bd = bwboundaries(fgMapWtColor > 250, 8, 'noholes');
    if length(bd)>=1
        img_fg2 = img;
        for bdi=1:length(bd)          
            img_fg2 = imposelabel(img_fg2, bd{bdi});
        end

        ii=fgMapWtColor==0;

%         for c=1:size(img_fg2, 3)            
%             tmp=img_fg2(:,:,c);
%             tmp(ii)=255;
%             img_fg2(:,:,c)=tmp;
%         end
%             img_fg2 = imposelabel(img_fg2, fixPt, 11, [255, 0, 0]);
        fg2_name=[sprintf('%s_%03d_fg2.jpg', name,j)];
        disp(fg2_name);
        imwrite(img_fg2, fullfile(output_dir, fg2_name));
    end
end

