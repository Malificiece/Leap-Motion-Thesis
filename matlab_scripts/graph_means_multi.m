function graph_means_multi(variable_order, keyboard_order, subject_order, col, num)
%
% Copyright (c) 2015, Garrett Benoit. All rights reserved.
%
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%   - Redistributions of source code must retain the above copyright
%     notice, this list of conditions and the following disclaimer.
%
%   - Redistributions in binary form must reproduce the above copyright
%     notice, this list of conditions and the following disclaimer in the
%     documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
% IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
% CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
% EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
% PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
% PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
% LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
% NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
% SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

KEYBOARD_NUM = size(keyboard_order,2);
SUBJECT_NUM = size(subject_order,2);
KEYBOARDS = 1:KEYBOARD_NUM;
COLORS = {[0 0.9 0.9],[1 0 1],[1 .65 0]};
LIGHTS = {[.65 0.7 0.7],[.7 .65 .7],[.7 .7 .65]};
%COLORS = {[1 0 1],[1 .65 0]};
%LIGHTS = {[.7 .65 .7],[.7 .7 .65]};
%COLORS = {[1 0 0],[0.2 0.85 0.2],[0 0 1],[1 0 1],[1 .65 0],[0 0.9 0.9]};
%LIGHTS = {[.7,.65,.65],[0.65 0.70 0.65],[.65 .65 .7],[.7 .65 .7],[.7 .7 .65],[.65 0.7 0.7]};
VARIABLE_NAMES = {'Text-Entry Rate', {'Text-Entry Rate', 'Modified Shortest-Transcribed'}, 'Minimum Word Distance', {'Minimum Word Distance', 'Modified Shortest-Transcribed'}, 'Keystrokes Per Character', {'Keystrokes Per Character', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Shortest-Transcribed'}, {'Minimum String Distance', 'Modified Backspace-Transcribed'}, 'Total Error Rate', {'Total Error Rate', 'Modified Shortest-Transcribed'}, 'Fréchet Distance', {'Fréchet Distance', 'Modified Shortest-Transcribed'}, {'Fréchet Distance', 'Modified Backspace-Transcribed'}, 'Word-Gesture Distance', {'Word-Gesture Distance', 'Modified Shortest-Transcribed'}, 'Word-Gesture Velocity', 'Hand Velocity', 'Word-Gesture Duration', {'Word-Gesture Duration', 'Modified Shortest-Transcribed'}, 'Reaction Time to Errors', 'Reaction Time to Simulate Touch', 'Reaction Time for First Correct Letter', 'Number of Touches Simulated', 'Number of Practice Words', 'Level of Discomfort', 'Level of Fatigue', 'Level of Difficulty', 'Preference Ranking'};
X_LABEL = 'Keyboard Input Device';
Y_LABELS = {'Words Per Minute (WPM)', 'Words Per Minute (WPM)', 'Error Rate (%)', 'Error Rate (%)', 'Key Strokes Per Character', 'Key Strokes Per Character', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Error Rate (%)', 'Distance (pixels)','Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Distance (pixels)', 'Velocity (pixels/s)', 'Velocity (cm/s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Time (s)', 'Number of Touches Detected', 'Number of Practice Words', 'Likert Scale', 'Likert Scale', 'Likert Scale', 'Rank'};
variables = {};
names = {};
y_labels = {};
for i=1:num
    variables = cat(1,variables,cell2mat(variable_order(:,col+(i-1))));
    names = cat(1,names,VARIABLE_NAMES(:,col+(i-1)));
    y_labels = cat(1,y_labels,cell2mat(Y_LABELS(:,col+(i-1))));
end

%calculate standard errors of the participant means
confs_array = {};
for i=1:num
    conf = {};
    var = cell2mat(variables(i));
    for j=1:KEYBOARD_NUM
        conf = cat(1,conf,((std(var(:,j))/sqrt(SUBJECT_NUM)) * 1.96));
    end
    confs_array = cat(1, confs_array, conf');
end

hold on
% draw bargraph first
var = {};
for i=1:KEYBOARD_NUM
   v = [];
   for j=1:num
       t = cell2mat(variables(j));
       v = cat(1,v,mean(t(:,i)));
   end
   var = cat(1,var,v');
end
b = bar(cell2mat(var), 1); %change value here to make it work
for i=1:num
   fc = cell2mat(LIGHTS(i)) * 2.5;
   fc = fc / max(fc);
   b(i).FaceColor = fc;
   b(i).EdgeColor = cell2mat(COLORS(i));
end
if(num==2)
    legend(b,{'non-modified','mod-shortest'})
    %legend(b,{'mod-shortest','mod-backspace'})
elseif(num==3)
    legend(b,{'non-modified','mod-shortest','mod-backspace'})
end

% draw error bars
if(num==2)
    for i=1:num
        if(mod(i,2)==0)
            errorb(KEYBOARDS + 0.14, mean(cell2mat(variables(i))), cell2mat(confs_array(i,:)));
        else
            errorb(KEYBOARDS - 0.14, mean(cell2mat(variables(i))), cell2mat(confs_array(i,:)));
        end
    end
elseif(num==3)
    for i=1:num
        if(i==1)
            errorb(KEYBOARDS - 0.23, mean(cell2mat(variables(i))), cell2mat(confs_array(i,:)));
        elseif(i==2)
            errorb(KEYBOARDS, mean(cell2mat(variables(i))), cell2mat(confs_array(i,:)));
        else
            errorb(KEYBOARDS + 0.23, mean(cell2mat(variables(i))), cell2mat(confs_array(i,:)));
        end
    end
end

% draw individual participant means on top
xscale = get(gca,'XLim');
xscale = xscale(2) - xscale(1);
yscale = get(gca,'YLim');
yscale = yscale(2) - yscale(1);
ratio = 0.0050;
xpixelsize = xscale * ratio;
%below was in loop
ypixelsize = (yscale*xpixelsize)/xscale;
ypixelsize = ypixelsize * 1.5; % this value unfortunately needs to change
for k=1:num
    var = cell2mat(variables(k));
    for i=1:KEYBOARD_NUM
       x = ones(SUBJECT_NUM,1) * i;
       y = var(:,i);
       for j=1:size(x)
           xpoints = [x(j) - xpixelsize, x(j) + xpixelsize, x(j) + xpixelsize, x(j) - xpixelsize];
           ypoints = [y(j) - ypixelsize, y(j) - ypixelsize, y(j) + ypixelsize, y(j) + ypixelsize];
           if(num==2)
               if(mod(k,2)==0)
                   pb=patch(xpoints + 0.14, ypoints, cell2mat(COLORS(k)),'edgecolor','none');
               else
                   pb=patch(xpoints - 0.14, ypoints, cell2mat(COLORS(k)),'edgecolor','none');
               end
           elseif(num==3)
               if(k==1)
                   pb=patch(xpoints - 0.23, ypoints, cell2mat(COLORS(k)),'edgecolor','none');
               elseif(k==2)
                   pb=patch(xpoints, ypoints, cell2mat(COLORS(k)),'edgecolor','none');
               else
                   pb=patch(xpoints + 0.23, ypoints, cell2mat(COLORS(k)),'edgecolor','none');
               end
           end
           alpha(pb,.5);
       end
    end
end

hold off
c = cat(1,keyboard_order',{''});
c = cat(1,{''},c);
set(gca, 'XTickLabel', c)
ax = gca;
ax.XTickLabelRotation = 45;
for i=1:num
    name = [names{i,:}]
    %title(name)
end
ylabel(y_labels(1))
xlabel(X_LABEL)