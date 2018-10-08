#! octave-interpreter-name -qf

results = cell (160, 3);
index = 1;

for i = 25:25:2000
	A=rand(i,i);
	B=rand(i,1);
	
	# start
	tic();

	# metoda bezposrednia
	A\B;

	# end
	time = toc();

	results(index,1) = "backslash";
	results(index,2) = i;
	results(index,3) = time;

	index += 1;
endfor

for i = 25:25:2000
	A=rand(i,i);
	B=rand(i,1);
	
	# start
	tic();

	# metoda LU
	[L,U,P] = lu(A);
	U\(L\flipud(B));


	# end
	time = toc();

	results(index,1) = "LU";
	results(index,2) = i;
	results(index,3) = time;

	index += 1;
endfor

fid = fopen ("times.txt", "w");
fprintf(fid, "alg,n,time\n");
fprintf(fid, "%s, %d, %f\n", results'{:});
fclose(fid);

printf("Done\n");




