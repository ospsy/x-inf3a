function [ seg_cluster, cluster_ind] = cluster_segs(segs, overlap_threshold)

nsegs = length(segs);

overlap_distance = zeros(nsegs, nsegs);

seg_ind = cell(nsegs, 1);

for i=1:nsegs
	seg_ind{i}=find(segs{i}>0);
end

for i=1:nsegs-1
	siz1=length(seg_ind{i});
	for j=(i+1):nsegs
		siz2=length(seg_ind{j});
        overlap = intersect(seg_ind{i}, seg_ind{j});
        d=1-2*length(overlap)/(siz1+siz2);
        overlap_distance(i,j)=d;
        overlap_distance(j,i)=d;
    end
end

Z = linkage(squareform(overlap_distance));
c = cluster(Z,'cutoff',1-overlap_threshold,'criterion','distance');

cs = unique(c);

seg_cluster = cell(length(cs),1);
cluster_method = 1;
cluster_ind=[];
for i=1:length(cs)
	ci=find(c==cs(i));
    segi=[];
    if cluster_method == 0 % union
        for j=1:length(ci)
            segi = union(segi, seg_ind{ci(j)});
        end
    elseif cluster_method == 1 %largest common segmentation
        sum_d = sum(overlap_distance(ci, ci), 2);
        [md,mi]=min(sum_d);
        segi = seg_ind{ci(mi)};
        cluster_ind=[cluster_ind , ci(mi) ];
    end
    seg = zeros(size(segs{1}));
    seg(segi)=255;
    seg_cluster{i}=seg;
end


