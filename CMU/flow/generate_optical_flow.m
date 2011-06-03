function generate_optical_flow(input_dir,starti,endi)

if ~exist('input_dir', 'var')
    fprintf('Need an input_dir\n');
    return
end

output_dir=fullfile(input_dir,'flow');
input_dir=fullfile(input_dir,'data');

if ~exist(input_dir, 'dir')
    fprintf('intput_dir does''nt exist\n');
    return
end

if ~exist(output_dir, 'dir')
    mkdir(output_dir);
end

filenames=dir([input_dir, '/capture_img_out_*.ppm']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/capture_img_out_*.jpg']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/capture_img_out_*.png']);
end

N=length(filenames);

if ~exist('starti', 'var')
    starti = 2;    
elseif ischar(starti)
    starti = str2num(starti);
end
starti=max([starti 1]);

if ~exist('endi', 'var')
    endi = N;   
elseif ischar(endi)
    endi = str2num(endi);    
end
endi=min([endi N-1]);

for k=starti:endi
    fprintf('Processing %s and %s\n',filenames(k).name,filenames(k+1).name);
    fname=[input_dir, '/', filenames(k).name];
    [pathstr, name, ext] = fileparts(fname);
    output_name=fullfile(output_dir, [name '.png'] );
    if ~exist(output_name,'file')
	    img1=double(imread(fname));
	    img2=double(imread(fullfile(input_dir, filenames(k+1).name)));
	    flow = mex_OF(img1, img2);
        flow=flow./40+0.5;
        flow(:,:,3)=zeros(size(flow,1),size(flow,2));
        imwrite(flow,output_name);
%         tmp=0.05*sqrt(flow(:,:,1).^2+flow(:,:,1).^2);
%         imwrite(tmp,[output_name '.png']);
    end
end

