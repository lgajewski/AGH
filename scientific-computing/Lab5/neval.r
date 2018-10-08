results = read.csv("f_osc.txt")

y_range = range(0, results$n)

pdf(file="osc_neval.pdf", height=5, width=10, bg="white")

plot(results[results$alg=="qag",]$a, results[results$alg=="qag",]$n, lty = 1, ylim = y_range, xlab = "a - parameter", ylab = "neval", col="white")

lines(results[results$alg=="qag",]$a, results[results$alg=="qag",]$n, lty = 1, col = "black")
lines(results[results$alg=="qags",]$a, results[results$alg=="qags",]$n, lty = 1, col = "blue")
lines(results[results$alg=="qagp",]$a, results[results$alg=="qagp",]$n, lty = 1, col = "red")
legend("topleft", c("qag", "qags", "qagp"), col=c("black", "blue","red"),lty=1)
dev.off()
