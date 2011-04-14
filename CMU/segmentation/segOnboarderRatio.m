function [ratio, cross]=segOnboarderRatio(bd, imheight, imwidth)

perimeter = 0;
onboarder = 0;
cross=0;
if length(bd)>=1
    for bdi=1:length(bd)
        perimeter = perimeter + length(bd{bdi});
        onboarder=onboarder+sum(abs(bd{bdi}(:,2)-1)<=1|abs(bd{bdi}(:,2)-imwidth)<=1|abs(bd{bdi}(:,1)-1)<=1|abs(bd{bdi}(:,1)-imheight)<=1);
        cross0=sum(abs(bd{bdi}(:,2)-1)<=1)>0;
        cross1=sum(abs(bd{bdi}(:,2)-imwidth)<=1)>0;
        cross2=sum(abs(bd{bdi}(:,1)-1)<=1)>0;
        cross3=sum(abs(bd{bdi}(:,1)-imheight)<=1)>0;
        cross=cross+cross0+cross1+cross2+cross3;
    end
end                

ratio = onboarder/perimeter;