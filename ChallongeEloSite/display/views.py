from django.shortcuts import render
from django.http import HttpResponse
from datetime import datetime

from . import models
from .models import *

# Create your views here.
def index(request):
	return render(request, 'display/index.html')

def rankings(request):
	#get latest time
	try:
		line = str(tournaments.objects.latest(field_name='date_started').date_started)
		t = datetime.strptime(line, '%Y-%m-%d')
	except ValueError as v:
		if len(v.args) > 0 and v.args[0].startswith('unconverted data remains: '):
			line = line[:-(len(v.args[0]) - 26)]
			t = datetime.strptime(line, '%Y-%m-%d')
		else:
			raise

	#get only the latest date, not date and time
	return render(request, 'display/rankings.html', {'lastDate': t})
