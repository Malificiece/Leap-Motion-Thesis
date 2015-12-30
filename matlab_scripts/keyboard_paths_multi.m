function keyboard_paths_multi(num,r,c,boolean)
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

DICTIONARIES = {{'sue', 'hot', 'got', 'fit', 'due', 'fir'}, {'tin', 'tub', 'rub', 'rim', 'gin', 'run'}, {'coy', 'dot', 'sir', 'soy', 'zit', 'sit'}, {'find', 'rubs', 'rind', 'fins', 'fibs', 'fine'}, {'mans', 'owns', 'lane', 'mane', 'pens', 'pans'}, {'core', 'fire', 'sire', 'sure', 'fore', 'dire'}, {'bale', 'vale', 'gape', 'gale', 'hale', 'tale'}, {'tales', 'bales', 'hales', 'games', 'gales', 'dales'}, {'shier', 'shies', 'shirt', 'shift', 'shire', 'shied'}, {'falls', 'galls', 'balls', 'gamma', 'calls', 'halls'}, {'sires', 'fores', 'sores', 'cores', 'fires', 'sites'}, {'dubbed', 'rubbed', 'subbed', 'finned', 'tinned', 'sinned'}, {'sicker', 'docket', 'socket', 'rocket', 'rocker', 'wicker'}, {'dipped', 'ripped', 'yipped', 'tipped', 'topped', 'hopped'}, {'hailed', 'mailed', 'bailed', 'jailed', 'nailed', 'failed'}};
%FRECHET = {{'eeg', 'egg', 'erg', 'ref', 'reg', 'rev', 'def', 'dry', 'eft', 'est'},{'eel', 'ell', 'elk', 'del', 'esp', 'reo', 'rep', 'sep', 'sol', 'sop'},{'min', 'kin', 'mob', 'mom', 'mon', 'mum', 'nib', 'non', 'nub', 'nun'},{'moos', 'moss', 'kids', 'kips', 'kiss', 'miss', 'mods', 'mood', 'mops', 'nips'},{'boos', 'boss', 'bias', 'bids', 'bios', 'boas', 'bois', 'bops', 'hips', 'hiss'},{'toll', 'tool', 'rill', 'roil', 'roll', 'rook', 'till', 'toil', 'took', 'yolk'},{'feel', 'fell', 'cell', 'dell', 'geek', 'crop', 'deep', 'desk', 'doll', 'dolo'},{'crees', 'cress', 'cerea', 'cered', 'crass', 'creed', 'crews', 'feeds', 'feted', 'fetes'},{'trees', 'tress', 'reads', 'reeds', 'rewed', 'teras', 'teres', 'terra', 'tread', 'treed'},{'tolls', 'tools', 'rills', 'roils', 'rolls', 'rooks', 'tills', 'toils', 'yolks', 'filed'},{'heels', 'hells', 'bells', 'gelds', 'jells', 'jerks', 'beeps', 'belle', 'below', 'broad'}, {'fusees', 'fusses', 'ciders', 'cussed', 'cusses', 'fizzed', 'fizzes', 'fussed', 'fuzzed', 'fuzzes'},{'rooter', 'rotter', 'reiter', 'report', 'retire', 'retort', 'rioter', 'terror', 'titter', 'tooter'},{'looses', 'losses', 'kidded', 'kissed', 'kisses', 'lidded', 'lizard', 'loaded', 'loosed', 'losers'},{'toller', 'tooler', 'ripple', 'roller', 'tiller', 'tipple', 'toiler', 'toilet', 'topple', 'triple'}};
FRECHET = {{'egg', 'erg', 'reg', 'def', 'dry', 'est'}, {'crass', 'creed', 'crews', 'feeds', 'feted', 'fetes'}};
ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
KEYBOARD = {[143, 180, 0], [513, 106, 0], [365, 106, 0], [291, 180, 0], [254, 254, 0], [365, 180, 0], [439, 180, 0], [513, 180, 0], [624, 254, 0], [587, 180, 0], [661, 180, 0], [735, 180, 0], [661, 106, 0], [587, 106, 0], [698, 254, 0], [772, 254, 0], [106, 254, 0], [328, 254, 0], [217, 180, 0], [402, 254, 0], [550, 254, 0], [439, 106, 0], [180, 254, 0], [291, 106, 0], [476, 254, 0], [217, 106, 0]};
COLORS = {[1 0 0],[0.2 0.85 0.2],[0 0 1],[1 0 1],[1 .85 0],[0 0.9 0.9]};

if boolean == true
    dictionary = [DICTIONARIES{num}];
else
    dictionary = [FRECHET{num}];
end

ds = size(dictionary);
img = imread('keyboard_multi.jpg');
subplot1(r,c,'Gap', [0 0], 'XTickL', 'None', 'YTickL', 'None');
hold on;
for i=1:ds(2)
    word = cell2mat(dictionary(i));
    % find x and y for word
    ws = size(word);
    x = [];
    y = [];
    for j=1:ws(2)
        for k=1:26
            if word(:,j) == [ALPHABET{k}]
                loc = [KEYBOARD{k}];
                x = cat(1,x,loc(:,1) - 58);
                y = cat(1,y,301 - loc(:,2) + (j - ws(2)/2)*7);
                break
            end
        end
    end
    x = x';
    y = y';
    if i == 10
        subplot1(i+1);
    else
        subplot1(i); 
    end
    axis equal;
    imshow(img);
    hold on;
    axis([0,760,0,241])
    plot(x,y,'Color',[1 .85 0],'LineWidth',4);
    hold off;
end
h = get(groot,'CurrentFigure');
set(h, 'Position', [100, 100, 760*c, 241*r]);
tightfig;