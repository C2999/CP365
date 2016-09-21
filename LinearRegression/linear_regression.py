import matplotlib.pyplot as plt
import numpy as np
np.random.seed(42) # Get the same random numbers every time


##very naive normalize. Removes any values deviating more than 8 degrees than the average temperature
def naiveNormalize(rec_high_temp, rec_avg_temp): 
	for num in range(0, len(rec_high_temp)):
		if (rec_high_temp[num] > (rec_avg_temp[num] + 8)) or (rec_high_temp[num] < (rec_avg_temp[num] + 8)):
			np.delete(rec_high_temp, num)
			np.delete(rec_avg_temp, num)

	return rec_high_temp, rec_avg_temp

def linearReg(iterations, learningRate, data):
	
	date = my_data[:, 0]
	djia_close = my_data[:, 1]
	_rec_high_temp = my_data[:,2]
	_rec_avg_temp = my_data[:,3]

	weight = np.random.rand(1)
	bias = np.random.rand(1)

	rec_high_temp, rec_avg_temp = naiveNormalize(_rec_high_temp, _rec_avg_temp)


	##computes the error and modifies the weight and bias accordingly
	for num in range(0, iterations):
		error = (rec_high_temp*weight+bias) - djia_close 
		weight = weight - np.sum((learningRate * error * rec_high_temp)/len(rec_high_temp))
		bias = bias - np.sum(learningRate * error * 1.0/len(rec_high_temp))
		endCost = np.sum(np.power((rec_high_temp*weight+bias) - djia_close, 2))
		print endCost


	





if __name__ == '__main__':
	my_data = np.genfromtxt('djia_temp.csv', delimiter=';', skip_header=1)[:21]
	linearReg(1000000, .001, my_data)
	##learning rate still allows steady decrease in endcost without rising back up 

