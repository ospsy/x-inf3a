function generate_optical_flow(input_dir,output_dir,starti,endi)

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
    starti = 2;    
elseif ischar(starti)
    starti = str2num(starti);
end
starti=max([starti 2]);

if ~exist('endi', 'var')
    endi = N;   
elseif ischar(endi)
    endi = str2num(endi);    
end
endi=min([endi N]);

for k=starti:endi
    fprintf('Processing %s and %s\n',filenames(k-1).name,filenames(k).name);
    fname=[input_dir, '/', filenames(k).name];
    [pathstr, name, ext] = fileparts(fname);
    output_name=fullfile(output_dir, [name '.flo']);
    if ~exist(output_name,'file')
	    img1=imread(fullfile(input_dir, filenames(k-1).name));    
	    img2=imread(fname);
	    [u, v] = optic_flow_brox(img1, img2, 80, 5, 3, 0.9,false);
	    [h,w,c]=size(img1);
	    write_flow(u,v, output_name)
	    img2=imcrop(img2,[h/10+1 w/10+1 8*w/10-1 8*h/10-1]);
	    imwrite(img2,fullfile(output_dir, filenames(k).name));
    end
end

%correction of the eye_positions file!
if (starti==2)
    img1=imread(fullfile(input_dir, filenames(1).name));
    [h,w,c]=size(img1);
    eye_pos=load(fullfile(input_dir,'eye_positions.txt'));
    fid = fopen(fullfile(output_dir,'eye_positions.txt'),'w');
    fprintf(fid,'%f %f\n',( eye_pos(2:size(eye_pos,1),:) - ones(size(eye_pos,1)-1,1)*[w/10,h/10])');
    fclose(fid);
end

path(p)

