function out = read_log_file(fname)

if nargin < 1
    fname = 'image_save/Log_data.txt';
end

fid = fopen(fname);
if fid < 0
    fprintf('Cannot read Log file');
    out = [];
    return;
end

while 1
    tline = fgetl(fid);
    if sum(findstr(tline,'# Person_file '))>0
        tline = fgetl(fid);
        fname_person = tline;
    elseif sum(findstr(tline,'# Scene_file'))>0
        tline = fgetl(fid);
        fname_scene = tline;
    elseif sum(findstr(tline,'# Edge'))>0
        tline = fgetl(fid);
        a = str2num( tline );
        Edge.sigma = a(1); Edge.Mag_thresh =a(2);
        Edge.Size  = a(3); Edge.n_arround = a(4); 
    elseif sum(findstr(tline,'# Eye Outside image size'))>0
        tline = fgetl(fid);
        a = str2num( tline );
        siz_Eyeimg = [a(2) a(1)];
        siz_Outimg = [a(4) a(3)];
    elseif sum(findstr(tline,'# Ellipse fitting '))>0
        tline = fgetl(fid);
        a = str2num( tline );
        EllipseFitting.n_grid = a(1);
        EllipseFitting.lambda = a(2);
        EllipseFitting.thres1 = a(3);
        EllipseFitting.thres2 = a(4);
        EllipseFitting.subweight = [a(5) a(5) a(6) a(6) a(7)];
    elseif sum(findstr(tline,'# Homography Matrix '))>0
        tline = fgetl(fid);
        a = str2num( tline );        
        HH = reshape(a,[3 3]);
    elseif sum(findstr(tline,'# TPS'))>0
        tline = fgetl(fid);
        a = str2num( tline );         
        TPS.lambda = a(1);TPS.ConstK = a(2);
    elseif sum(findstr(tline,'# Compensation'))>0
        tline = fgetl(fid);
        a = str2num( tline ); 
        dxy = [a(1)  a(2)];
        Offset_xy = [ a(3) a(4)];
    elseif sum(findstr(tline,'# num'))>0
        Data=[];
        while 1
            tline = fgetl(fid);
            if ~ischar(tline)
                break;
            else
                Data = [Data;str2num( tline )];
            end
        end
        break;
    end
end
out.fname_person = fname_person;
out.fname_scene  = fname_scene;
out.Edge = Edge;
out.EllipseFitting = EllipseFitting;
out.Homo = HH;
out.TPS  = TPS;
out.Data = Data;
out.TPS  = TPS;
out.dxy  = dxy;
out.Offset_xy  = Offset_xy;
out.siz_Eyeimg = siz_Eyeimg; 
out.siz_Outimg = siz_Outimg;

fclose(fid);