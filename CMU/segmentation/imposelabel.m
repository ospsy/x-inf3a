function img = imposelabel(img, label, si, color)

if ~exist('si', 'var') || isempty(si)
    si = 5;
end

if ~exist('color', 'var') || isempty(color)
    color = [0, 255, 0];
end

n=size(label,1);
tmp=ones(n,1);
label=round(min(tmp*[size(img,1) size(img,2)],max(tmp*[1 1],label)));

idx=sub2ind([size(img, 1), size(img, 2)], label(:,1), label(:,2));

bi=zeros(size(img, 1), size(img, 2));
bi(idx)=255;
se = strel('ball',si,si);
bi = imdilate(bi,se);
idx = find(bi>128);

if size(img, 3)==1
    newimg(:,:,1)=img;
    newimg(:,:,2)=img;
    newimg(:,:,3)=img;
    img=newimg;
end

img1=img(:,:,1);
img1(idx) = color(1);
img2=img(:,:,2);
img2(idx) = color(2);
img3=img(:,:,3);
img3(idx) = color(3);

img(:,:,1) = img1;
img(:,:,2) = img2;
img(:,:,3) = img3;

end
