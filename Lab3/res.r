results = read.csv("result.txt")
avg_results = aggregate( time ~ n:alg, data=results, FUN=mean)
avg_results$sd = aggregate( time ~ n:alg, data=results, FUN=sd)$time

y_range = range(0, results$time)`

pdf(file="result.pdf", height=5, width=10, bg="white")

plot(avg_results[avg_results$alg=="ver1",]$n, avg_results[avg_results$alg=="ver1",]$time, 
	lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in seconds", col="blue")


lines(avg_results[avg_results$alg=="ver2",]$n, avg_results[avg_results$alg=="ver2",]$time, lty = 2, col = "red")
lines(avg_results[avg_results$alg=="gsl",]$n, avg_results[avg_results$alg=="gsl",]$time, lty = 3, col = "green")

legend("topleft", c("ver1","ver2", "gsl"),col=c("red","green", "blue"),lty=1:3)


errbar(avg_results[avg_results$alg=="ver1",]$n,
	avg_results[avg_results$alg=="ver1",]$time,
	avg_results[avg_results$alg=="ver1",]$time-avg_results[avg_results$alg=="ver1",]$sd,
	avg_results[avg_results$alg=="ver1",]$time+avg_results[avg_results$alg=="ver1",]$sd,
	xlab="n", ylab="Computing time in seconds", add=TRUE)


errbar(avg_results[avg_results$alg=="ver2",]$n,
	avg_results[avg_results$alg=="ver2",]$time,
	avg_results[avg_results$alg=="ver2",]$time-avg_results[avg_results$alg=="ver2",]$sd,
	avg_results[avg_results$alg=="ver2",]$time+avg_results[avg_results$alg=="ver2",]$sd,
	xlab="n", ylab="Computing time in seconds", add=TRUE)

errbar(avg_results[avg_results$alg=="gsl",]$n,
	avg_results[avg_results$alg=="gsl",]$time,
	avg_results[avg_results$alg=="gsl",]$time-avg_results[avg_results$alg=="gsl",]$sd,
	avg_results[avg_results$alg=="gsl",]$time+avg_results[avg_results$alg=="gsl",]$sd,
	xlab="n", ylab="Computing time in seconds", add=TRUE)

ver1_n = avg_results[avg_results$alg=="ver1",]$n
ver1_t = avg_results[avg_results$alg=="ver1",]$time

fit1 <- lm(ver1_t ~ poly(ver1_n, 3, raw=TRUE))
seq1 <- seq(100,300, length.out=250)
lines(seq1, predict(fit1, data.frame(ver1_n=seq1)), col='blue')



ver2_n = avg_results[avg_results$alg=="ver2",]$n
ver2_t = avg_results[avg_results$alg=="ver2",]$time

fit2 <- lm(ver2_t ~ poly(ver2_n, 3, raw=TRUE))
seq2 <- seq(100,300, length.out=250)
lines(seq2, predict(fit2, data.frame(ver2_n=seq2)), col='red')



gsl_n = avg_results[avg_results$alg=="gsl",]$n
gsl_t = avg_results[avg_results$alg=="gsl",]$time

fit3 <- lm(gsl_t ~ poly(gsl_n, 3, raw=TRUE))
seq3 <- seq(100,300, length.out=250)
lines(seq3, predict(fit3, data.frame(gsl_n=seq3)), col='green')

dev.off()
