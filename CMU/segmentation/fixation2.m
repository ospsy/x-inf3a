% Generate the fixations files for the images in input_dir based on the
% eye_positions.txt file
function fixation2(input_dir,grid, starti, endi)

if ~exist('input_dir', 'var')
    disp('No input_dir...')
    return;
end

output_dir=[input_dir, '/output'];
if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

fix_dir=[output_dir, '/fix'];
if(~exist(fix_dir, 'dir'))
    mkdir(fix_dir);
end

if ~exist('grid','var')
    grid=[0,0 ; [40*cos(pi/4:pi/4:2*pi) ; 40*sin(pi/4:pi/4:2*pi)]' ; [80*cos(pi/4:pi/4:2*pi) ; 80*sin(pi/4:pi/4:2*pi)]' ];
elseif grid==0
    grid=[0,0];
elseif grid==1
    grid=[0,0 ; [20*cos(pi/2:pi/2:2*pi) ; 20*sin(pi/2:pi/2:2*pi)]'];
end

filenames=dir([input_dir, '/*.ppm']);
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.jpg']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.png']);
end
if(length(filenames)==0)
    filenames=dir([input_dir, '/*.bmp']);
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

filename=fullfile(input_dir, 'save.mat');
load(filename);
eye_pos=[eye_pos(:,1),eye_pos(:,2)];

for i=starti:endi,    
    imgFileName=fullfile(input_dir, names(i,:));
    
    disp(imgFileName);

    [pathstr, name, ext] = fileparts(imgFileName);
    
    fix_txt = [name, '_fix.txt'];
    fid = fopen(fullfile(fix_dir,fix_txt),'w');
    fprintf(fid,'%f %f\n',(ones(size(grid,1),1)*eye_pos(i,:)+grid)');
    fclose(fid);
end
end
