results = read.csv("f_times.txt")
avg_results = aggregate( time ~ n:alg, data=results, FUN=mean)
avg_results$sd = aggregate( time ~ n:alg, data=results, FUN=sd)$time

y_range = range(0, avg_results$time)

pdf(file="times1.pdf", height=5, width=10, bg="white")

plot(avg_results$n, avg_results$time, main="All methods - time comparison",
	lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in miliseconds", col="black")


lines(avg_results[avg_results$alg=="LU",]$n, avg_results[avg_results$alg=="LU",]$time, lty = 1, col = "blue")
lines(avg_results[avg_results$alg=="Cholesky",]$n, avg_results[avg_results$alg=="Cholesky",]$time, lty = 1, col = "red")
legend("topleft", c("LU", "Cholesky"), col=c("blue","red"),lty=1)

seq1 <- seq(10, 650, length.out=250)

errbar(avg_results[avg_results$alg=="LU",]$n,
	avg_results[avg_results$alg=="LU",]$time,
	avg_results[avg_results$alg=="LU",]$time-avg_results[avg_results$alg=="LU",]$sd,
	avg_results[avg_results$alg=="LU",]$time+avg_results[avg_results$alg=="LU",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "LU")

LU_n = avg_results[avg_results$alg=="LU",]$n
LU_t = avg_results[avg_results$alg=="LU",]$time

fit1 <- lm(LU_t ~ poly(LU_n, 3, raw=TRUE))
lines(seq1, predict(fit1, data.frame(LU_n=seq1)), col='blue')



errbar(avg_results[avg_results$alg=="Cholesky",]$n,
	avg_results[avg_results$alg=="Cholesky",]$time,
	avg_results[avg_results$alg=="Cholesky",]$time-avg_results[avg_results$alg=="Cholesky",]$sd,
	avg_results[avg_results$alg=="Cholesky",]$time+avg_results[avg_results$alg=="Cholesky",]$sd,
	main = "", xlab="n", ylab="Computing time in miliseconds", add=FALSE)
title(main = "Cholesky")
Cholesky_n = avg_results[avg_results$alg=="Cholesky",]$n
Cholesky_t = avg_results[avg_results$alg=="Cholesky",]$time

fit2 <- lm(Cholesky_t ~ poly(Cholesky_n, 3, raw=TRUE))
lines(seq1, predict(fit2, data.frame(Cholesky_n=seq1)), col='red')



dev.off()
