function keyboard_paths()
WORD_ORDER = {'sue', 'hot', 'got', 'fit', 'due', 'fir', 'tin', 'tub', 'rub', 'rim', 'gin', 'run', 'coy', 'dot', 'sir', 'soy', 'zit', 'sit', 'find', 'rubs', 'rind', 'fins', 'fibs', 'fine', 'mans', 'owns', 'lane', 'mane', 'pens', 'pans', 'core', 'fire', 'sire', 'sure', 'fore', 'dire', 'bale', 'vale', 'gape', 'gale', 'hale', 'tale', 'tales', 'bales', 'hales', 'games', 'gales', 'dales', 'shier', 'shies', 'shirt', 'shift', 'shire', 'shied', 'falls', 'galls', 'balls', 'gamma', 'calls', 'halls', 'sires', 'fores', 'sores', 'cores', 'fires', 'sites', 'dubbed', 'rubbed', 'subbed', 'finned', 'tinned', 'sinned', 'sicker', 'docket', 'socket', 'rocket', 'rocker', 'wicker', 'dipped', 'ripped', 'yipped', 'tipped', 'topped', 'hopped', 'hailed', 'mailed', 'bailed', 'jailed', 'nailed', 'failed'};
ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
KEYBOARD = {[143, 180, 0], [513, 106, 0], [365, 106, 0], [291, 180, 0], [254, 254, 0], [365, 180, 0], [439, 180, 0], [513, 180, 0], [624, 254, 0], [587, 180, 0], [661, 180, 0], [735, 180, 0], [661, 106, 0], [587, 106, 0], [698, 254, 0], [772, 254, 0], [106, 254, 0], [328, 254, 0], [217, 180, 0], [402, 254, 0], [550, 254, 0], [439, 106, 0], [180, 254, 0], [291, 106, 0], [476, 254, 0], [217, 106, 0]};
COLOR = [1 .85 0];

for resize=1:1 %change end of loop to 2 for this and below loop to fix sue & got resizing wrong
for num=1:size(WORD_ORDER,2)
    word = [WORD_ORDER{num}];
    fid=fopen('word_paths.txt');
    data = textscan(fid, '%s', 1, 'delimiter', '\n', 'headerlines', num-1); % read from file
    fclose(fid);

    data = [data{1}];
    data = cell2mat(data);
    data(1) = '';
    data(1) = '';
    data = data(1:end-2);
    data = regexp(data, '\},\s\{', 'split');
    paths = {};
    for i=1:size(data,2)
        path = {};
        %parse stuff
        p = [data{i}];
        p(1) = '';
        p = p(1:end-1);
        p = regexp(p, '\],\[', 'split');
        for j=1:size(p,2)
            vec = [];
            v = [p{j}];
            v = regexp(v, ',', 'split');
            for k=1:size(v,2)-1
                val = str2double([v{k}]);
                vec = cat(1,vec,val);
            end
            path = cat(2,path,vec');
        end
        pstruct = cell2struct(path,{'vec'},size(path,2));
        paths = cat(2,paths,pstruct);
    end

    axis equal;
    img = imread('keyboard.jpg');
    imshow(img);
    hold on;

    % find x and y for word
    ws = size(word);
    x = [];
    y = [];
    for j=1:ws(2)
        for k=1:26
            if word(:,j) == [ALPHABET{k}]
                loc = [KEYBOARD{k}];
                x = cat(1,x,loc(:,1) - 58);
                y = cat(1,y,301 - loc(:,2));
                break
            end
        end
    end
    x = x';
    y = y';
    plot(x,y,'Color',COLOR,'LineWidth',4)
                %'MarkerEdgeColor','k',...
                %'MarkerFaceColor',COLOR,...
                %'MarkerSize',10,'Marker','o');
    ColorSet = varycolor(18);%(varycolor(18) + 1) / 2; %make the colors way brighter since we have dark background
    set(gca, 'ColorOrder', ColorSet);

    hold all;
    % for each one draw participant path
    ps = size(paths);
    for i=1:ps(2)
        path = [paths{i}];
        s = size(path);
        x = [];
        y = [];
        for j=1:s(2)
            loc = path(j).vec;
            x = cat(1,x,loc(:,1) - 58);
            y = cat(1,y,301 - loc(:,2));
        end
        x = x';
        y = y';
        p=plot(x,y,'LineWidth',2,'LineStyle',':',...
                    'MarkerSize',3,'Marker','o')
        p.MarkerFaceColor = p.Color;
    end

    % set max x and y
    axis([0,982,0,241])
    hold off;
    
    h = get(groot,'CurrentFigure');
    set(h, 'Position', [100, 100, 982*2, 241*2]);
    tightfig;
    
    filename = strcat('fig_',word);
    filename = strcat(filename,'_paths');
    print(filename,'-dpng')
end
end