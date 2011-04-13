function region=preprocessRegion(region)

se = strel('disk',6,4);
region1 = imclose(region,se);
region1=imfill(region1, 'holes');
region2 = imclose(region1,se);
region=region2;
