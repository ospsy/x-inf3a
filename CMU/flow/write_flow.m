function write_flow(u,v,name)

fid = fopen(name, 'w');

fwrite(fid, 202021.25, 'float32');
[h,w]=size(u);
fprintf('%i %i\n',w,h);

fwrite(fid,w,'int');
fwrite(fid,h,'int');

fwrite(fid,v , 'float32',4);
fseek(fid,11,'bof');
fwrite(fid,u , 'float32',4);

fclose(fid);

fprintf('Finished writing %s\n',name);

end