
function segmentSelection(input_dir)

% addpath('../../../shared');

output_dir=[input_dir, '/output'];
img_dir=input_dir;
seg_dir=[output_dir, '/segs/'];

select_dir=[output_dir, '/segs/select/'];
if ~exist(select_dir, 'dir')
    mkdir(select_dir);
end

onboarder_threshold= 0.1;
minimum_segsize = 1600;
elong_factor=10;

junk_dir=[output_dir, '/segs/junk/'];
if ~exist(junk_dir, 'dir')
    mkdir(junk_dir);
end

filenames=dir([input_dir, '/*.jpg']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end

nimg=length(filenames);

for i=1:nimg %imi:imi    
    img_file=fullfile(img_dir, filenames(i).name);
    [pathstr, name, ext, versn] = fileparts(img_file);    
    res_files=dir([seg_dir, sprintf('/%s_*_res.mat', name)]);
    if length(res_files)>0
        im=imread(img_file);
        imheight=size(im, 1);
        imwidth=size(im, 2);
    end        
    for j=1:length(res_files)        
        mask_file = fullfile(seg_dir, res_files(j).name);  
        A=sscanf(res_files(j).name, '%05d_%03d_res.mat');
        ii=A(1);
        jj=A(2);
        if exist(mask_file, 'file')                
            tic;
            disp(mask_file);
            load(mask_file, 'fgMapWtColor');
            s=sum(fgMapWtColor(:)>0);
                        
            bd = bwboundaries(fgMapWtColor > 0); 
                        
            [ratio, cross]=segOnboarderRatio(bd, imheight, imwidth);
            
            STATS = regionprops(double(logical(fgMapWtColor > 0)), {'Centroid', 'Solidity', 'MajorAxisLength', 'MinorAxisLength'});
            p=round(STATS(1).Centroid);            
                        
            if s>minimum_segsize && ratio<onboarder_threshold &&STATS(1).Solidity>0.5 ...
               &&p(1)>0&&p(1)<=imwidth&&p(2)>0&&p(2)<=imheight&&fgMapWtColor(p(2), p(1))>0 ...
               &&STATS(1).MajorAxisLength<elong_factor*STATS(1).MinorAxisLength && cross<2
                tgt_dir = select_dir;                    
            else
                tgt_dir = junk_dir;
            end 
                    
            src =fullfile(seg_dir, sprintf('%s_%03d_fg2.jpg', name, jj));                     
            tgt =fullfile(tgt_dir, sprintf('%s_%03d_fg2.jpg', name, jj));                                 
            disp(src);
            disp(tgt);
            copyfile(src, tgt);
            
            src =fullfile(seg_dir, sprintf('%s_%03d_res.mat', name, jj));                     
            tgt =fullfile(tgt_dir, sprintf('%s_%03d_res.mat', name, jj));                     
            disp(src);
            disp(tgt);
            copyfile(src, tgt);                        
            toc
        end
    end	
end
