avg_results = read.csv("times.txt")

y_range = range(0, avg_results$time)

pdf(file="times.pdf", height=5, width=10, bg="white")

plot(avg_results$n, avg_results$time, main="All methods - time comparison",
	lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in miliseconds", col="black")


lines(avg_results[avg_results$alg=="LU",]$n, avg_results[avg_results$alg=="LU",]$time, lty = 1, col = "blue")
lines(avg_results[avg_results$alg=="backslash",]$n, avg_results[avg_results$alg=="backslash",]$time, lty = 1, col = "red")
legend("topleft", c("LU", "backslash"), col=c("blue","red"),lty=1)


dev.off()
