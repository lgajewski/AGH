results = read.csv("f_interp.txt")
points = read.csv("f_points.txt")

y_range = range(0, points$y)

pdf(file="plots/points.pdf", height=5, width=10, bg="white")

plot(points$x, points$y, lty = 1, ylim = y_range, xlab = "x", ylab = "y", col="black")

# lines(results[results$alg=="polynomial",]$x, results[results$alg=="polynomial",]$y, lty = 1, col = "blue")
# lines(results[results$alg=="cspline",]$x, results[results$alg=="cspline",]$y, lty = 1, col = "red")
# lines(results[results$alg=="akima",]$x, results[results$alg=="akima",]$y, lty = 1, col = "green")
# lines(results[results$alg=="lagrange",]$x, results[results$alg=="lagrange",]$y, lty = 1, col = "gray")
# lines(results[results$alg=="newton",]$x, results[results$alg=="newton",]$y, lty = 1, col = "orange")
# legend("topleft", c("polynomial", "cspline", "akima", "lagrange", "newton"), col=c("blue","red", "green", "gray", "orange"),lty=1)
dev.off()
