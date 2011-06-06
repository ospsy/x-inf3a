function img = drawCross(img,x,y,color)
    w=size(img,2);
    h=size(img,1);
    img(max(min((-20:20)+round(y),h),1),max(min((-1:1)+round(x),w),1),1)=color(1);
    img(max(min((-20:20)+round(y),h),1),max(min((-1:1)+round(x),w),1),2)=color(2);
    img(max(min((-20:20)+round(y),h),1),max(min((-1:1)+round(x),w),1),3)=color(3);
    img(max(min((-1:1)+round(y),h),1),max(min((-20:20)+round(x),w),1),1)=color(1);
    img(max(min((-1:1)+round(y),h),1),max(min((-20:20)+round(x),w),1),2)=color(2);
    img(max(min((-1:1)+round(y),h),1),max(min((-20:20)+round(x),w),1),3)=color(3);
    
end