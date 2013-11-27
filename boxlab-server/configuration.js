module.exports = {
	//connectivity variables
    zwave_host: "http://localhost:8083/",
    master_host: "http://176.31.185.109:8083/",
    
    //dashboard variables
    minDuration: (30 * 1000),
    maxDuration: (3 * 60 * 1000),

	//device manager variables
    deviceUpdateTicks: 30,
    deviceMultilevelTicks: 10,
    deviceBatteryTicks: 10,
    deviceMeterTicks: 5,
    tickRate: 1000
};