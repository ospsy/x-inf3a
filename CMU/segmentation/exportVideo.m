function exportVideo( input_dir, segs_dir, output_dir )

if ~exist('input_dir', 'var') || ~exist('segs_dir', 'var') || ~exist('output_dir', 'var')
    disp('Need the 3 variables');
    exit;
end

input_dir=fullfile(input_dir,'data');
if ~exist(input_dir, 'dir') || ~exist(segs_dir, 'dir')
    disp('Directories don''t exist');
    exit;
end

if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

logs=read_log_file([input_dir '/image_save/Log_data.txt']);
file=[input_dir '/../save.mat'];
if ~exist(file,'file')
    disp('No save.mat file');
    exit;
end

load(file);

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
fixs=logs.Data(:,8:9)+ones(size(logs.Data,1),1)*logs.Offset_xy;

n=1;
for i=1:N
    if strcmp(names(n,:),filenames(i).name)
        target=[segs_dir '/' names(n,:)];
        color=[0 255 0];
        if ~exist(target,'file')
            target=[input_dir '/' filenames(i).name];
        end
        n=n+1;
    else
        color=[255 0 0];
        target=[input_dir '/' filenames(i).name];
    end
    img=imread(target);
    img = drawCross(img,fixs(i,1),fixs(i,2),color);
    imwrite(img,[output_dir '/' filenames(i).name]);
end

end
