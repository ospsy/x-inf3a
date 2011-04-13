function run_segment(input_dir, starti, endi)

if ~exist('starti', 'var') || ~exist('endi', 'var')
%     fixation(input_dir);
    seg(input_dir);
else
%     fixation(input_dir, starti, endi);
    seg(input_dir, 1, starti, endi);
end

