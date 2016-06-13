function plot_egress_time(filePath) 
	data = load(filePath);
	data = [(1:1:rows(data))' data];
	
	plot(data(:, 1), data(:, 2), '.k','markersize', 15);

	axis([0 101 0 40])
	title('Tiempo de egreso', 'fontsize', 20);
	xlabel('Peaton egresados [peatones]', 'fontsize', 20);
	ylabel('Tiempo [s]', 'fontsize', 20);
	set(gca, 'fontsize', 10);
	set(gca, 'XTick', [0:10:100])
	set(gca, 'YTick', [0:5:40])
	set(gca, 'fontsize', 20);
end
