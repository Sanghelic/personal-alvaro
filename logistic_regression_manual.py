import numpy as np

class LogisticRegressionScratch:
    def __init__(self, lr=0.01, n_iter=1000):
        self.lr = lr
        self.n_iter = n_iter
        self.weights = None
        self.bias = None

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-z))

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.weights = np.zeros(n_features)
        self.bias = 0
        for _ in range(self.n_iter):
            linear_model = np.dot(X, self.weights) + self.bias
            y_predicted = self.sigmoid(linear_model)
            dw = (1 / n_samples) * np.dot(X.T, (y_predicted - y))
            db = (1 / n_samples) * np.sum(y_predicted - y)
            self.weights -= self.lr * dw
            self.bias -= self.lr * db

    def predict(self, X):
        linear_model = np.dot(X, self.weights) + self.bias
        y_predicted = self.sigmoid(linear_model)
        return np.where(y_predicted >= 0.5, 1, 0)

def main():
    X = np.array([
        [0.2, 0.3],
        [0.4, 0.6],
        [0.1, 0.8],
        [0.7, 0.9],
        [0.9, 0.2],
        [0.8, 0.5]
    ])
    y = np.array([0, 0, 0, 1, 1, 1])
    model = LogisticRegressionScratch(lr=0.1, n_iter=1000)
    model.fit(X, y)
    predictions = model.predict(X)
    print("Etiquetas reales:", y)
    print("Predicciones modelo:", predictions)
    acc = np.mean(predictions == y)
    print("Accuracy:", acc)

if __name__ == "__main__":
    main()
