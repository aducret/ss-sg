function plot_egress_time(N, vd, M) 

	for m=1:M
		filePath = strcat("doc/examples/flow", N, "-", vd, "-", m, ".txt");
		times(m, :) = load(filePath)';
	end
	
	plot(data(:, 1), data(:, 2), '.k','markersize', 15);

	timeMax = ceil(max(data(:, 2)));
	peopleAmount = rows(data);

	axis([0 peopleAmount 0 timeMax])
	title('Tiempo de egreso', 'fontsize', 20);
	xlabel('Peaton egresados [peatones]', 'fontsize', 20);
	ylabel('Tiempo [s]', 'fontsize', 20);
	set(gca, 'fontsize', 10);
	set(gca, 'XTick', [0:10:100])
	set(gca, 'YTick', [0:5:40])
	set(gca, 'fontsize', 20);
end
