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

fid=fopen([input_dir '/fixs3.txt']);
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

n=1;
for i=1:N
    [d name ext]=fileparts(filenames(i).name);
    filenames2=dir([segs_dir '/' name '_*_fg2.jpg']);
    img=imread([input_dir '/' filenames(i).name]);
    N2=length(filenames2);
    fprintf('%i segs found\n',N2);
    for j=1:N2
        im2=imread([segs_dir '/' filenames2(j).name]);
        t=find(im2(:,:,1)<25 & im2(:,:,3)<25 & im2(:,:,2)>230);
        [x,y]=ind2sub([size(im2,1),size(im2,2)],t);
        t2=sub2ind(size(im2),x,y,1*ones(size(x)));
        img(t2)=0;
        t2=sub2ind(size(im2),x,y,2*ones(size(x)));
        img(t2)=255;
        t2=sub2ind(size(im2),x,y,3*ones(size(x)));
        img(t2)=0;
    end
    img=drawCross(img,eye_pos(i,1),eye_pos(i,2),[255 0 0]);
    for k=1:size(fixationsTracked(i).fixs,1)
        img=drawCross(img,fixationsTracked(i).fixs(k,1),fixationsTracked(i).fixs(k,2),[0 255 0]);
    end
    imwrite(img,[output_dir '/' filenames(i).name]);
end

end
