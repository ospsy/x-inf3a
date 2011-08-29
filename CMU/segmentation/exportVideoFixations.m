function exportVideoFixations( input_dir,output_dir)

if ~exist('input_dir', 'var') || ~exist('output_dir', 'var')
    disp('Need the input_dir & output_dir');
    exit;
end

input_dir=fullfile(input_dir,'data');
if ~exist(input_dir, 'dir')
    disp('input_dir/data do not exist');
    exit;
end

if(~exist(output_dir, 'dir'))
    mkdir(output_dir);
end

fixs_file=[input_dir '/fixs3.txt'];
if ~exist(fixs_file, 'file')
    disp('fixs3.txt do not exist');
    exit;
end
fid=fopen(fixs_file);
m=0;
while ~feof(fid)
    a=sscanf(fgetl(fid),'%i %i',[2 inf]);
    a=a';
    m=m+1;
    fixationsTracked(m).fixs=a;
end
fclose(fid);

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
logs=read_log_file([input_dir '/image_save/Log_data.txt']);
eye_pos=logs.Data(:,8:9)+ones(size(logs.Data,1),1)*logs.Offset_xy;

for i=1:N
    input_name=fullfile(input_dir,filenames(i).name);
    output_name=sprintf('%s/%i.jpg',output_dir,i);
    img=imread(input_name);
    img=drawCross(img,eye_pos(i,1),eye_pos(i,2),[255 0 0]);
    for k=1:size(fixationsTracked(i).fixs,1)
        img=drawCross(img,fixationsTracked(i).fixs(k,1),fixationsTracked(i).fixs(k,2),[0 255 0]);
    end
    imwrite(img,output_name);
end

end
