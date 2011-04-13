function mergeSegments(input_dir1, input_dir2, output_dir)

filenames=dir([input_dir1, '/*.jpg']);
if(length(filenames)==0)
    filenames=dir([input_dir1, '/*.png']);
end

seg_dir1=[input_dir1, '/output/segs/select/'];
seg_dir2=[input_dir2, '/output/segs/select/'];

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

for i=1:length(filenames),
    imgFileName=fullfile(input_dir1, filenames(i).name);    
    disp(imgFileName);
    
    [pathstr, name, ext, versn] = fileparts(imgFileName);
    
    resfiles1=dir(sprintf('%s/%s_*_res.mat', seg_dir1, name));
    resfiles2=dir(sprintf('%s/%s_*_res.mat', seg_dir2, name));
    
    if ~exist(tmp_dir, 'dir')
        mkdir(tmp_dir);
        disp(['tmp_dir:', tmp_dir]);
    else
        delete([tmp_dir, '/*.*']);
    end 
    
    cnt = 0;
    for j=1:length(resfiles1)
        cnt=cnt+1;
        srcfile=fullfile(seg_dir1, resfiles1(j).name);
        tgtfile=fullfile(tmp_dir, sprintf('%s_%03d_res.mat', name, cnt));
        copyfile(srcfile, tgtfile);
    end
    
    for j=1:length(resfiles2)
        cnt=cnt+1;
        srcfile=fullfile(seg_dir2, resfiles2(j).name);
        tgtfile=fullfile(tmp_dir, sprintf('%s_%03d_res.mat', name, cnt));
        copyfile(srcfile, tgtfile);
    end
    
    disp('Clustering');    
    img_dir=pathstr;
    segmentClustering(name, tmp_dir, output_dir, img_dir);
end