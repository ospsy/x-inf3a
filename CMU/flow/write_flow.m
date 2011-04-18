function write_flow(u,v,name)

fid = fopen(name, 'w');

fwrite(fid, 202021.25, 'float32');
[h,w]=size(u);
fprintf('%i %i\n',w,h);

fwrite(fid,w,'int');
fwrite(fid,h,'int');

for y=1:h
    for x=1:w
        fwrite(fid,u(y,x) , 'float32');
        fwrite(fid,v(y,x) , 'float32');
    end
end
%fwrite(fid,v , 'float32',4);
%fseek(fid,11,'bof');
%fwrite(fid,u , 'float32',4);
fclose(fid);

fprintf('Finished writing %s\n',name);

end