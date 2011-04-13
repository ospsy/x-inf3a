function renderSegment(input_dir)

output_dir=[input_dir, '/output'];
fix_dir=[output_dir, '/fix'];
seg_dir=[output_dir, '/segs/'];
match_dir=[output_dir, '/match/'];
alignment_dir=[output_dir, '/alignment/'];

render_dir=[seg_dir, '/render/'];
if(~exist(render_dir, 'dir'))
    mkdir(render_dir);
end

filenames=dir([input_dir, '/*.jpg']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end

N=length(filenames);

for i=1:length(filenames)
    imgFileName=fullfile(input_dir, filenames(i).name);    
    disp(imgFileName);    
    [pathstr, name, ext, versn] = fileparts(imgFileName);
    
    img=imread(imgFileName);
    
    resfiles=dir(sprintf('%s/%s_*_res.mat', seg_dir, name));    
    for j=1:length(resfiles)    
        res_mat = fullfile(seg_dir, resfiles(j).name);
        if exist(res_mat, 'file')
           load(res_mat, 'fgMapWtColor');           
           tic;
           bd = bwboundaries(fgMapWtColor > 0); 
           toc
           
           for k=1:length(bd)
               img = imposelabel(img, bd{k}, 3);
               img = imposelabel(img, bd{k}, 2, [0, 0, 0]);
           end
        end
    end    
    
    imshow(img);
    
    out_file=fullfile(render_dir, [name, '.png']);
    imwrite(img, out_file);
    
%     pause();
end