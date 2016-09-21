import matplotlib.pyplot as plt
import math
import numpy as np
from random import randint

np.random.seed(42)







class Cluster:

    def __init__(self, centroid, clusterDic):
        self.centroid = centroid
        self.clusterDic = clusterDic

    ##adds new value to cluster
    def appendToClusterDic(self, movieKey, movieVal):
        self.clusterDic[movieKey] = movieVal
    ##recalculates centroid by averaging each user's rating for each movie in the current cluster. 
    def recalculateCentroid(self):
        updatedCentroid = {}
        for user, rating in self.centroid.iteritems():
            cost = 0
            validUsers = 0;
            for movieID, movieDic in self.clusterDic.iteritems():
                if user in movieDic: 
                    cost += movieDic[user]
                    validUsers += 1
            if cost != 0: updatedRating = cost/validUsers
            else: updatedRating = 0 
            updatedCentroid[user] = updatedRating
        return updatedCentroid

    ##def printCluster(self):



class ClusterModel:

    def __init__(self, k, mainDic, userID, centroidsArr, clusterArr):
        self.k = k ##number of clusters
        self.mainDic = mainDic ##dictionary of all movies
        self.userID = userID
        self.centroidsArr = centroidsArr ##array of centroid dictionaries
        self.clusterArr = clusterArr ##array of cluster objects

    ##calculates random centroids for initial run
    def makeRandomCentroid(self):
        for i in range(self.k):
            i = {}
            for j in  range(len(self.userID)):
                i[userID[j]] = randint( 0, 5 )
            self.centroidsArr.append(i)

    ##passes in the centroid to clusters that have no movies added yet
    def makeNewClusters(self):
        for i in range(self.k):
            emptyDict = {}
            newCluster = Cluster(self.centroidsArr[i], emptyDict)
            self.clusterArr.append(newCluster)
    ##goes through mainDic and appends each movie to the cluster with the 
    ## centroid most alike its ratings
    def evaluateClusters(self):
        for key, value in self.mainDic.iteritems():
            bestCluster = self.clusterArr[0]
            bestCost = 10000
            for cluster in self.clusterArr:
                currVal = self.movieDistance( cluster.centroid, value )
                if( currVal < bestCost ):
                    bestCost = currVal
                    bestCluster = cluster
            bestCluster.appendToClusterDic( key, value )

    ##computes distance between 2 movie dictionaries by seeing difference in each users rating
    ##skips 0 values in both
    def movieDistance(self, movie1, movie2 ):
        cost = 0
        for key, value in movie1.iteritems():
            if key in movie2:
                cost += (movie1[key] - movie2[key]) ** 2
        return cost

    def calculateClusterError(self, cluster):
        error = 0
        for key, value in cluster.clusterDic.iteritems():
            error += self.movieDistance(value, cluster.centroid)
        return error

    def calculateAllError(self):
        totalerror = 0
        for cluster in self.clusterArr:
            totalerror += self.calculateClusterError(cluster)
        print "total Error:"
        print totalerror



    def printModel(self, i):
        for cluster in self.clusterArr:
            val = "Movie Cluster: "
            for key, value in cluster.clusterDic.iteritems():
                val += str(key) + ", "
            print "Error: " + str(self.calculateClusterError(cluster))
            print "Epoch #: " + str(i)
            ##print "Centroid: " + str(cluster.centroid)
            print val




    ##makes random centroids, inputs them into clusters, and then appends 
    ##each movie to the cluster with the centroid closest to its values
    def initialize(self):

        self.makeRandomCentroid()
        self.makeNewClusters()
        self.evaluateClusters()

    ##Recalculates each centroid and recomputes the clusters accordingly
    def train(self, epochs):
        for i in range(epochs):
            newCentroidsArr = []
            for j in range(len(self.clusterArr)):
                newCentroidsArr.append(self.clusterArr[j].recalculateCentroid())
            self.centroidsArr = newCentroidsArr
            self.clusterArr = []
            self.makeNewClusters()
            self.evaluateClusters()
            self.calculateAllError()
            ##self.printModel(i)


#returns 3 NP arrays that correspond to the UserID, MovieID, and Rating
def loadDataset(filename="u.data"):
    my_data = np.genfromtxt(filename, skip_header=0)
    userID = my_data[:, 0]
    movieID = my_data[:, 1]
    rating = my_data[:, 2]
    return userID, movieID, rating

##makes dataset 'mainDic' which is the main dictionary that has a movie as a key, and another dictionary as
##its value which stores each user and their rating for that movie.
def makeDataSet(userID, movieID, rating):
    mainDic = {}
    for i in range(len(movieID)):
        if movieID[i] in mainDic: 
            mainDic[movieID[i]].update({userID[i]: rating[i]})   
        else:
            mainDic[movieID[i]] = {userID[i]: rating[i]}
    return mainDic



if __name__=="__main__":
    userID, movieID, rating = loadDataset()
    mainDic = makeDataSet( userID, movieID, rating )


    emptyCentroidsArr = []
    emptyClusterArr = []
    ##creates the model
    movieRatingsModel = ClusterModel(8, mainDic, userID, emptyCentroidsArr, emptyClusterArr)
    ##initializes the model by creating random centroids and clustering the movies accordingly 
    movieRatingsModel.initialize()
    ##Clusters the set based on number of epochs 
    movieRatingsModel.train(100)





