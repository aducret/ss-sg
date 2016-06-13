function plot_flow(filePath, maxWindows) 
	data = load(filePath);
	
	tMax = ceil(max(data));
	rta = zeros(columns(0:0.1:tMax), 1)';
	windows = 1;
	step = 0.1;
	i = 0;

	for t = 0:step:tMax
		if (windows < maxWindows && floor(t) > windows)
			windows++;
		end
		for j = 1:rows(data) 				
			if (data(j) <= t && data(j) >= (t-windows))
				rta(i) = rta(i) + 1;
			end
		end
		i++;
	end
	
	rta1 = rta(1:columns(0:step:windows-step));
	rta2 = rta(columns(0:step:windows):end);
	windows = step;
	for i = 1:columns(rta1) 
		rta1(i) = rta(i) / windows;
		windows = windows + step;
	end
	rta2 = rta2 / windows;
	rta = [rta1 rta2];

	plot(0:step:tMax, rta, '.k', 'linewidth', 2,'markersize', 15);

	axis([0 tMax 0 ceil(max(rta))])
	title('Caudal', 'fontsize', 20);
	xlabel('Tiempo [s]', 'fontsize', 20);
	ylabel('Caudal [peatones / s]', 'fontsize', 20);
	set(gca, 'fontsize', 20);
	set(gca, 'XTick', [0:5:tMax])
	set(gca, 'YTick', [0:1:ceil(max(rta))])
	set(gca, 'fontsize', 20);
end
