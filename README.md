# FingerMap
Apk for a fp05 biometric device

#Report Generation
clock in, clock out and otherclocks, refference the student class, 
here is the logic:
	`students: [{
		id:1,
		firstName:laban,
		lastName:Kering,
		dob:1989,
		classLevel:std1,
		fingerThumb:....,
		fingerIndex:....,
		schoolID:....,
		guardian:{
			firstName:...,
			lastName:....,
			nationalID:....,
			physicalAddress:....,
			emailAddress:....
		},
		clockIn:[{
			timeStamps:....,
			clockOut: {
				timeStamp:....,
			}
		}]	
	},
		id:2,
		regNo:....,
		firstName:laban,
		lastName:Kering,
		dob:1989,
		classLevel:std1,
		fingerThumb:....,
		fingerIndex:....,
		schoolID:....,
		guardian:{
			firstName:...,
			lastName:....,
			nationalID:....,
			phoneNumber:...,
			physicalAddress:....,
			emailAddress:....
		},
		clockIn:[{
			timeStamps:....,
			clockOut: {
				timeStamp:....,
			}
		}, 
			{
			timeStamps:....,
			clockOut: {
				timeStamp:....,
			}
		}]
	}]`
Now watch closely-->
	`students.clockIn.clockOut`

	
