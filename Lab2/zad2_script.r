results = read.csv("zad2_results.txt")
avg_results = aggregate( time ~ n:alg, data=results, FUN=mean)
avg_results$sd = aggregate( time ~ n:alg, data=results, FUN=sd)$time

y_range = range(0, results$time)


pdf(file="zad2.pdf", height=5, width=10, bg="white")

plot(avg_results[avg_results$alg=="b1",]$n, avg_results[avg_results$alg=="b1",]$time, type="o", lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in seconds", col="blue")
lines(avg_results[avg_results$alg=="b2",]$n, avg_results[avg_results$alg=="b2",]$time, type="o", lty = 2, col = "red")
legend("topleft", c("b1","b2"),col=c("blue","red"),lty=1:2)


errbar(avg_results[avg_results$alg=="b1",]$n, 
	avg_results[avg_results$alg=="b1",]$time, 
	avg_results[avg_results$alg=="b1",]$time-avg_results[avg_results$alg=="b1",]$sd, 
	avg_results[avg_results$alg=="b1",]$time+avg_results[avg_results$alg=="b1",]$sd, 
	xlab="n", ylab="Computing time in seconds", add=TRUE)


errbar(avg_results[avg_results$alg=="b2",]$n, 
	avg_results[avg_results$alg=="b2",]$time, 
	avg_results[avg_results$alg=="b2",]$time-avg_results[avg_results$alg=="b2",]$sd, 
	avg_results[avg_results$alg=="b2",]$time+avg_results[avg_results$alg=="b2",]$sd, 
	xlab="n", ylab="Computing time in seconds", add=TRUE)

dev.off()
