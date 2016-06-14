function plot_flow(N, vd, M, maxWindows) 

	for m=1:M
		filePath = strcat("doc/examples/flow", num2str(N), "-", num2str(vd), "-", num2str(m), ".txt");
		times(:, m) = load(filePath);
	end

	for i=1:N
		meanTimes(i) = mean(times(i, :));
		stdTimes(i) = std(times(i, :));
	end

	meanTimes = meanTimes';
	stdTimes = stdTimes';

	step = 0.1;
	flow1 = get_flow(meanTimes - stdTimes, step, maxWindows);
	flow2 = get_flow(meanTimes, step, maxWindows);
	flow3 = get_flow(meanTimes + stdTimes, step, maxWindows);
	minCols = min([columns(flow1) columns(flow2) columns(flow3)]);

	flow1 = flow1(1:minCols);
	flow2 = flow2(1:minCols);
	flow3 = flow3(1:minCols);

	tMax = ceil(max(meanTimes + stdTimes));
	plotDomain = 0:step:tMax;
	plotDomain = plotDomain(1:minCols);

	plot(plotDomain, flow1, '-b;caudal medio menos desvio estandar;', 'linewidth', 2,'markersize', 15);
	hold on;
	plot(plotDomain, flow2, '-k;caudal medio;', 'linewidth', 2,'markersize', 15);
	plot(plotDomain, flow3, '-r;caudal medio mas desvio estandar;', 'linewidth', 2,'markersize', 15);

	axis([0 tMax 0 ceil(max(flow3))])
	title('Caudal', 'fontsize', 20);
	xlabel('Tiempo [s]', 'fontsize', 20);
	ylabel('Caudal [peatones / s]', 'fontsize', 20);
	set(gca, 'fontsize', 20);
	set(gca, 'XTick', [0:5:tMax])
	set(gca, 'YTick', [0:1:ceil(max(flow3))])
	set(gca, 'fontsize', 20);
end

function flow = get_flow(data, step, maxWindows)
	tMax = ceil(max(data));
	rta = zeros(columns(0:step:tMax), 1)';
	windows = 1;
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
	flow = rta;
end