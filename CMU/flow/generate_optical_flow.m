function generate_optical_flow(input_dir,output_dir)

p=path;
path(p,'./brox')

if ~exist('input_dir', 'var')
    fprintf('Need an input_dir\n');
    return
end

if ~exist('output_dir', 'var')
    fprintf('Need an output_dir\n');
    return
end

if ~exist(input_dir, 'dir')
    fprintf('intput_dir does''nt exist\n');
    return
end

if ~exist(output_dir, 'dir')
    mkdir(output_dir);
    return
end

filenames=dir([input_dir, '/*.ppm']);
if(length(filenames)==0))
    filenames=dir([input_dir, '/*.jpg']);
end

img2=imread([input_dir, '/', filenames(1).name]);

for k=2:length(filenames)
    fprintf('Processing %s and %s\n',filenames(k-1).name,filenames(k).name);
    img1=img2;
    fname=[input_dir, '/', filenames(k).name];
    img2=imread(fname);
    [pathstr, name, ext] = fileparts(fname);
    [u, v] = optic_flow_brox(img1, img2, 10, 100, 3, 0.8,false);
    [h,w,c]=size(img1);
    %u2=zeros(h,w);
    %v2=zeros(h,w);
    %u2(h/10+1:h*9/10,w/10+1:9*w/10)=u;
    %v2(h/10+1:h*9/10,w/10+1:9*w/10)=v;
    write_flow(u,v, fullfile(output_dir, [name '.flo']))
    img2=imcrop(img2,[h/10+1 w/10+1 8*w/10-1 8*h/10-1]);
    imwrite(img2,fullfile(output_dir, filenames(k).name));
end

path(p)

end
