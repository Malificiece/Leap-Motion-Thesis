function thesis_stats(variable_order, keyboard_order, col)
%HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE = [0; 1; 0; 1; 0; 1; 1; 1; 1; 1; 0;
%1; 0; 1; 0; 1; 0; 0]; %final
HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE = [0; 1; 1; 1; 0; 0; 1]; %pilot
VARIABLE_NAMES = {'Text-Entry Rate', {'Text-Entry Rate', 'Modified Shortest-Transcribed'}, 'Minimum Word Distance', {'Minimum Word Distance', 'Modified Shortest-Transcribed'}, 'Keystrokes Per Character', {'Keystrokes Per Character', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Backspace-Transcribed'}, 'Total Error Rate', {'Total Error Rate', 'Modified Shortest-Transcribed'}, 'Fréchet Distance', {'Fréchet Distance', 'Modified Shortest-Transcribed'}, {'Fréchet Distance', 'Modified Backspace-Transcribed'}, 'Word-Gesture Distance', {'Word-Gesture Distance', 'Modified Shortest-Transcribed'}, 'Word-Gesture Velocity', 'Hand Velocity', 'Word-Gesture Duration', {'Word-Gesture Duration', 'Modified Shortest-Transcribed'}, 'Reaction Time to Errors', 'Reaction Time to Simulate Touch', 'Reaction Time for First Correct Letter', 'Number of Touches Simulated', 'Number of Practice Words', 'Level of Discomfort', 'Level of Fatigue', 'Level of Difficulty', 'Preference Ranking'};
X_LABEL = 'Keyboard Input Device';
Y_LABELS = {'Words Per Minute (WPM)', 'Words Per Minute (WPM)', 'Error Rate (%)', 'Error Rate (%)', 'Key Strokes Per Character', 'Key Strokes Per Character', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Distance (pixels)','Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Velocity (pixels/s)', 'Velocity (cm/s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Number of Touches Detected', 'Number of Practice Words', 'Likert Scale', 'Likert Scale', 'Likert Scale', 'Rank'};
variable = cell2mat(variable_order(:,col));
name = VARIABLE_NAMES(:,col);
y_label = cell2mat(Y_LABELS(:,col));

[p,tbl,stats] = anova1(variable, keyboard_order);
p
tbl
means = getfield(stats, 'means');
stds = std(variable(:,1));
[~,colCount] = size(variable);
col = 2;
while col <= colCount
    stds = cat(1,stds,std(variable(:,col)));
    col = col + 1;
end
[means; stds']
getfield(stats, 's')
if p <= 0.05
	c = multcompare(stats,'Display','off')
end

col = 1;
while col <= colCount
    keyboard = keyboard_order(1,col)
    [p,tbl,stats] = anovan(variable(:,col), {HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE},'Display','off');
    p
    if p <= 0.05
        tbl
        c = multcompare(stats,'Display','off')
    end
    col = col + 1;
end

ax = gca;
ax.XTickLabelRotation = 45;
name = [name{:}]
%title(name)
ylabel(y_label)
xlabel(X_LABEL)