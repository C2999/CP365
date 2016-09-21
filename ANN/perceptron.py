import matplotlib.pyplot as plt
import numpy as np
np.random.seed(42)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def dsigmoid(x):
    return x * (1 - x)


class Model:

    def __init__(self, layer1_size, layer2_size, learning_rate=0.1 ):
        self.layer1_size = layer1_size
        self.layer2_size = layer2_size
        self.learning_rate = learning_rate
        self.layer1 = Layer(layer1_size, layer2_size, learning_rate)
        self.layer2 = Layer(layer2_size, 1, learning_rate)


    def iteration(self, X, y):
        ##gradientChecker(self.layer1,  X, y)
        out1 = self.layer1.forward(X)
        out2 = self.layer2.forward(out1)


        
        deriv_err1 = self.layer2.calculateDerivError(y, out2)
        ##print deriv_err1.shape
        ##print y.shape
        ##print out2.shape



        deriv_err2 = self.layer2.backward(deriv_err1).T
        self.layer1.backward(deriv_err2)


    def reportAccuracy(self,X,y):
        out1 = self.layer1.forward(X)
        out2 = self.layer2.forward(out1)

        out2 = np.round(out2)
        ##newY = np.reshape( y, ( 683, 1 ) )
        
        count = np.count_nonzero(y - out2)
        correct = len(X) - count
        print "%.4f" % (float(correct)*100.0 / len(X))



    def train(self, X, y, number_epochs):
        for i in range(number_epochs):
            self.iteration(X, y)
            self.reportAccuracy(X,y)


class Layer:

    def __init__(self, input_size, output_size,  learning_rate=0.1):
        self.weights = np.random.rand(input_size, output_size)
        self.learning_rate = learning_rate

    def forward(self, X):
        self.incoming = X
        act = X.dot(self.weights)
        act = sigmoid(act)
        self.outputs = act
        return act

    def backward(self, err):
        err = err * dsigmoid(self.outputs)
        update = self.learning_rate * self.incoming.T.dot(err)
        ##formatted_update = np.reshape(update.mean(axis=1), (len(self.weights), 1))
        newDelta = self.weights.dot(err.T)
        self.weights +=  update
        return newDelta

    def calculateDerivError(self, y, pred):
        return 2*(y - pred)

    def calculateError(self, y, pred):
        return (np.sum(np.power((y - pred), 2)))



def loadDataset(filename='breast_cancer.csv'):
    my_data = np.genfromtxt(filename, delimiter=',', skip_header=1)

    # The labels of the cases
    # Raw labels are either 4 (cancer) or 2 (no cancer)
    # Normalize these classes to 0/1
    y = (my_data[:, 10] / 2) - 1

    # Case features
    X = my_data[:, :10]

    # Normalize the features to (0, 1)
    X_norm = np.round(X / X.max(axis=0))

    return X_norm, y



def gradientChecker( model, X, y):
    epsilon = 1E-5

    model.weights[1] += epsilon
    out1 = model.forward(X)
    err1 = model.calculateError(y, out1)

    model.weights[1] -= 2*epsilon
    out2 = model.forward(X)
    err2 = model.calculateError(y, out2)

    numeric = (err2 - err1) / (2*epsilon)
    print numeric

    model.weights[1] += epsilon
    out3 = model.forward(X)
    err3 = model.calculateDerivError(y, out3)
    derivs = model.backward(err3)
    print derivs[1]





if __name__=="__main__":
    X, y = loadDataset()
    # X = X
    print X
    print y
    print X.shape, y.shape
    model = Model(10, 25, 0.001)
    ##gradientChecker(model, X, y)
    y = np.reshape(y, (683, 1))
    model.train(X, y, 1000)