results = read.csv("zad1_results.txt")


y_range = range(0, results$time)


pdf(file="zad1.pdf", height=5, width=10, bg="white")

plot(results[results$alg=="b1",]$time, type="o", lty = 1, ylim = y_range, xlab = "n", ylab = "Computing time in seconds", col="blue")
lines(results[results$alg=="b2",]$time, type="o", lty = 2, col = "red")
legend("topleft", c("b1","b2"),col=c("blue","red"),lty=1:2)
dev.off()
