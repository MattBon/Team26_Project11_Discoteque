all:
	/Utenti/MattiaBonomi/Download/android-ndk-r10e/ndk-build.cmd
	mkdir ../tmp
	cp ..\libs\armeabi\libpdBeatDetection.so ..\tmp\pdbeatdetection.pd_linux
	cd ..\tmp && zip externals.zip *.pd_linux && mv externals.zip ..\res\raw
	rm -rf ..\tmp	
