results = read.csv("f_times_25_300.txt")
avg_results = aggregate( time ~ n:alg, data=results, FUN=mean)
avg_results$sd = aggregate( time ~ n:alg, data=results, FUN=sd)$time

y_range = range(0, avg_results$time)

pdf(file="plots/times.pdf", height=5, width=10, bg="white")

plot(avg_results$n, avg_results$time, main="All methods - time comparison",
	lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in miliseconds", col="black")


lines(avg_results[avg_results$alg=="polynomial",]$n, avg_results[avg_results$alg=="polynomial",]$time, lty = 1, col = "blue")
lines(avg_results[avg_results$alg=="cspline",]$n, avg_results[avg_results$alg=="cspline",]$time, lty = 1, col = "red")
lines(avg_results[avg_results$alg=="akima",]$n, avg_results[avg_results$alg=="akima",]$time, lty = 1, col = "green")
lines(avg_results[avg_results$alg=="lagrange",]$n, avg_results[avg_results$alg=="lagrange",]$time, lty = 1, col = "gray")
lines(avg_results[avg_results$alg=="newton",]$n, avg_results[avg_results$alg=="newton",]$time, lty = 1, col = "orange")
legend("topleft", c("polynomial", "cspline", "akima", "lagrange", "newton"), col=c("blue","red", "green", "gray", "orange"),lty=1)

seq1 <- seq(10, 300, length.out=250)

errbar(avg_results[avg_results$alg=="polynomial",]$n,
	avg_results[avg_results$alg=="polynomial",]$time,
	avg_results[avg_results$alg=="polynomial",]$time-avg_results[avg_results$alg=="polynomial",]$sd,
	avg_results[avg_results$alg=="polynomial",]$time+avg_results[avg_results$alg=="polynomial",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "polynomial")

polynomial_n = avg_results[avg_results$alg=="polynomial",]$n
polynomial_t = avg_results[avg_results$alg=="polynomial",]$time

fit1 <- lm(polynomial_t ~ poly(polynomial_n, 3, raw=TRUE))
lines(seq1, predict(fit1, data.frame(polynomial_n=seq1)), col='blue')





errbar(avg_results[avg_results$alg=="lagrange",]$n,
	avg_results[avg_results$alg=="lagrange",]$time,
	avg_results[avg_results$alg=="lagrange",]$time-avg_results[avg_results$alg=="lagrange",]$sd,
	avg_results[avg_results$alg=="lagrange",]$time+avg_results[avg_results$alg=="lagrange",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "lagrange")
lagrange_n = avg_results[avg_results$alg=="lagrange",]$n
lagrange_t = avg_results[avg_results$alg=="lagrange",]$time

fit2 <- lm(lagrange_t ~ poly(lagrange_n, 3, raw=TRUE))
lines(seq1, predict(fit2, data.frame(lagrange_n=seq1)), col='red')



errbar(avg_results[avg_results$alg=="newton",]$n,
	avg_results[avg_results$alg=="newton",]$time,
	avg_results[avg_results$alg=="newton",]$time-avg_results[avg_results$alg=="newton",]$sd,
	avg_results[avg_results$alg=="newton",]$time+avg_results[avg_results$alg=="newton",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "newton")
newton_n = avg_results[avg_results$alg=="newton",]$n
newton_t = avg_results[avg_results$alg=="newton",]$time

fit3 <- lm(newton_t ~ poly(newton_n, 3, raw=TRUE))
lines(seq1, predict(fit3, data.frame(newton_n=seq1)), col='green')



errbar(avg_results[avg_results$alg=="akima",]$n,
	avg_results[avg_results$alg=="akima",]$time,
	avg_results[avg_results$alg=="akima",]$time-avg_results[avg_results$alg=="akima",]$sd,
	avg_results[avg_results$alg=="akima",]$time+avg_results[avg_results$alg=="akima",]$sd,
	main = "", xlab="n", ylab="Computing time in microseconds", add=FALSE)
title(main = "akima")
akima_n = avg_results[avg_results$alg=="akima",]$n
akima_t = avg_results[avg_results$alg=="akima",]$time

fit4 <- lm(akima_t ~ poly(akima_n, 3, raw=TRUE))
lines(seq1, predict(fit4, data.frame(akima_n=seq1)), col='gray')



errbar(avg_results[avg_results$alg=="cspline",]$n,
	avg_results[avg_results$alg=="cspline",]$time,
	avg_results[avg_results$alg=="cspline",]$time-avg_results[avg_results$alg=="cspline",]$sd,
	avg_results[avg_results$alg=="cspline",]$time+avg_results[avg_results$alg=="cspline",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "cspline")
cspline_n = avg_results[avg_results$alg=="cspline",]$n
cspline_t = avg_results[avg_results$alg=="cspline",]$time

fit5 <- lm(cspline_t ~ poly(cspline_n, 3, raw=TRUE))
lines(seq1, predict(fit5, data.frame(cspline_n=seq1)), col='orange')

dev.off()
