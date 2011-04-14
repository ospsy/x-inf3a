function img=imposeLine(img,x,y,color,N)

if length(x)~=2 || length(y)~=2
    return;
end

if ~exist('color', 'var')||isempty(color)
    color = 'r';
end

if ischar(color)
    switch color
        case 'r'
            c=[255,0,0]';
        case 'g'
            c=[0,255,0]';
        case 'b'
            c=[0,0,255]';
        case 'w'
            c=[255, 255, 255]';
    end
elseif length(color)==3
    c=color(:);
end

if ~exist('N', 'var')||isempty(N)
    N = max(10, 10*sqrt(distSqr([x(1);y(1)],[x(2);y(2)])));
end

h=size(img, 1);
w=size(img, 2);
ch=size(img,3);
if ch==1    
    tmp=zeros(h,w,ch);
    tmp(:,:,1)=img;
    tmp(:,:,2)=img;
    tmp(:,:,3)=img;
    img=uint8(tmp);
end

j=x(:);
xx=[1,N]';
xxi=(1:N)';
jj=round(interp1q(xx,j,xxi));

i=y(:);
xx=[1,N]';
xxi=(1:N)';
ii=round(interp1q(xx,i,xxi));

idx=logical(ii>0&ii<size(img,1)&jj>0&jj<size(img,2));
ii=ii(idx);
jj=jj(idx);

i=sub2ind([size(img,1), size(img,2)], ii,jj);
imm=img(:,:,1);
imm(i)=c(1);
img(:,:,1)=imm;
imm=img(:,:,2);
imm(i)=c(2);
img(:,:,2)=imm;
imm=img(:,:,3);
imm(i)=c(3);
img(:,:,3)=imm;

i=sub2ind([size(img,1), size(img,2)], ii(logical((jj-1)>0&(jj-1)<size(img,2))),jj(logical((jj-1)>0&(jj-1)<size(img,2)))-1);
imm=img(:,:,1);
imm(i)=c(1);
img(:,:,1)=imm;
imm=img(:,:,2);
imm(i)=c(2);
img(:,:,2)=imm;
imm=img(:,:,3);
imm(i)=c(3);
img(:,:,3)=imm;

i=sub2ind([size(img,1), size(img,2)], ii(logical((jj+1)>0&(jj+1)<size(img,2))),jj(logical((jj+1)>0&(jj+1)<size(img,2)))+1);
imm=img(:,:,1);
imm(i)=c(1);
img(:,:,1)=imm;
imm=img(:,:,2);
imm(i)=c(2);
img(:,:,2)=imm;
imm=img(:,:,3);
imm(i)=c(3);
img(:,:,3)=imm;        
