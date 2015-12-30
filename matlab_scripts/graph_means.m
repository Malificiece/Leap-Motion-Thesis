function graph_means(variable_order, keyboard_order, subject_order, col)
KEYBOARD_NUM = size(keyboard_order,2);
KEYBOARDS = 1:KEYBOARD_NUM;
SUBJECT_NUM = size(subject_order,2);
COLORS = {[1 0 0],[0.2 0.85 0.2],[0 0 1],[1 .65 0],[1 0 1],[0 0.9 0.9],[0.75,0.45,0.2]};
LIGHTS = {[.7,.65,.65],[0.65 0.70 0.65],[.65 .65 .7],[.7 .7 .65],[.7 .65 .7],[.65 0.7 0.7],[.7,.65,.6]};
%COLORS = {[0.75,0.45,0.2],[1 0 0],[0.2 0.85 0.2],[0 0 1],[1 0 1],[1 .65 0],[0 0.9 0.9]};
%LIGHTS = {[.7,.65,.6],[.7,.65,.65],[0.65 0.70 0.65],[.65 .65 .7],[.7 .65 .7],[.7 .7 .65],[.65 0.7 0.7]};
VARIABLE_NAMES = {'Text-Entry Rate', {'Text-Entry Rate', 'Modified Shortest-Transcribed'}, 'Minimum Word Distance', {'Minimum Word Distance', 'Modified Shortest-Transcribed'}, 'Keystrokes Per Character', {'Keystrokes Per Character', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Backspace-Transcribed'}, 'Total Error Rate', {'Total Error Rate', 'Modified Shortest-Transcribed'}, 'Fréchet Distance', {'Fréchet Distance', 'Modified Shortest-Transcribed'}, {'Fréchet Distance', 'Modified Backspace-Transcribed'}, 'Word-Gesture Distance', {'Word-Gesture Distance', 'Modified Shortest-Transcribed'}, 'Word-Gesture Velocity', 'Hand Velocity', 'Word-Gesture Duration', {'Word-Gesture Duration', 'Modified Shortest-Transcribed'}, 'Reaction Time to Errors', 'Reaction Time to Simulate Touch', 'Reaction Time for First Correct Letter', 'Number of Touches Simulated', 'Number of Practice Words', 'Level of Discomfort', 'Level of Fatigue', 'Level of Difficulty', 'Preference Ranking'};
X_LABEL = 'Keyboard Input Device';
Y_LABELS = {'Words Per Minute (WPM)', 'Words Per Minute (WPM)', 'Error Rate (%)', 'Error Rate (%)', 'Key Strokes Per Character', 'Key Strokes Per Character', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Distance (pixels)','Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Velocity (pixels/s)', 'Velocity (cm/s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Number of Touches Detected', 'Number of Practice Words', 'Likert Scale', 'Likert Scale', 'Likert Scale', 'Rank'};
variable = cell2mat(variable_order(:,col));
name = VARIABLE_NAMES(:,col);
y_label = cell2mat(Y_LABELS(:,col));

%calculate standard errors of the participant means
%variable(:,1);
confs = (std(variable(:,1))/sqrt(SUBJECT_NUM)) * 1.96;
count = 2;
while count <= KEYBOARD_NUM
   confs = cat(1,confs,((std(variable(:,count))/sqrt(SUBJECT_NUM)) * 1.96));
   count = count + 1;
end

hold on
% draw bargraph first
count = 1;
while count <= KEYBOARD_NUM
   %scatter(count, mean(variable(:,count)), 40, cell2mat(COLORS(count)), 's', 'filled');
   fc = cell2mat(LIGHTS(count)) * 2.5;
   fc = fc / max(fc);
   bar(count, mean(variable(:,count)),0.5,'FaceColor',fc,'EdgeColor',cell2mat(COLORS(count)))
   count = count + 1;
end

% draw error bars
errorb(KEYBOARDS, mean(variable), confs);

% draw individual participant means on top
xscale = get(gca,'XLim');
xscale = xscale(2) - xscale(1);
yscale = get(gca,'YLim');
yscale = yscale(2) - yscale(1);
ratio = 0.0050;
xpixelsize = xscale * ratio;
count = 1;
while count <= KEYBOARD_NUM
   fc = cell2mat(COLORS(count));
   %scatter(ones(SUBJECT_NUM,1) * count, variable(:,count), 15, fc, 's', 'filled')
   x = ones(SUBJECT_NUM,1) * count;
   y = variable(:,count);
   t = 0:pi/10:2*pi;
   for i=1:size(x)
       ypixelsize = (yscale*xpixelsize)/xscale;
       ypixelsize = ypixelsize * 1.6; % this value unfortunately needs to change
       xpoints = [x(i) - xpixelsize, x(i) + xpixelsize, x(i) + xpixelsize, x(i) - xpixelsize];
       ypoints = [y(i) - ypixelsize, y(i) - ypixelsize, y(i) + ypixelsize, y(i) + ypixelsize];
       ec = cell2mat(COLORS(count)) * 1.5 * .75;
       ec = ec / max(ec);
       pb=patch(xpoints, ypoints,cell2mat(COLORS(count)),'edgecolor','none');
       %pb=patch((sin(t)/30 + x(i)),(cos(t)/8 + y(i)),cell2mat(COLORS(count)),'edgecolor','none');
       alpha(pb,.5);
   end
   count = count + 1;
end

hold off
c = cat(1,keyboard_order',{''});
c = cat(1,{''},c);
set(gca, 'XTickLabel', c)
ax = gca;
ax.XTickLabelRotation = 45;
name = [name{:}]
%title(name)
ylabel(y_label)
xlabel(X_LABEL)