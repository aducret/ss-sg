function plot_egress_time(N, vd, M) 

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
	
	data1 = meanTimes - stdTimes;
	data2 = meanTimes;
	data3 = meanTimes + stdTimes;
	plotDomain = 1:rows(meanTimes);

	plot(plotDomain, data1, '-b','markersize', 15,'linewidth', 2);
	hold on;
	plot(plotDomain, data2, '-k','markersize', 15,'linewidth', 2);
	plot(plotDomain, data3, '-r','markersize', 15,'linewidth', 2);

	timeMax = ceil(max(data3));
	peopleAmount = rows(meanTimes);

	axis([0 peopleAmount 0 timeMax])
	title('Tiempo de egreso', 'fontsize', 20);
	xlabel('Peaton egresados [peatones]', 'fontsize', 20);
	ylabel('Tiempo [s]', 'fontsize', 20);
	set(gca, 'fontsize', 20);
	set(gca, 'XTick', [0:10:peopleAmount])
	set(gca, 'YTick', [0:5:timeMax])
	set(gca, 'fontsize', 20);
end
