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
	line = str(tournaments.objects.latest(field_name='date_started').date_started)
	line = line[:10]

	#get only the latest date, not date and time

	return render(request, 'display/rankings.html', {'lastDate': line, 'players': players.objects.exclude(name__icontains=" and ").order_by('-elo')})
def doubles_rankings(request):
	#get latest time
	line = str(tournaments.objects.latest(field_name='date_started').date_started)
	line = line[:10]

	#get only the latest date, not date and time
	return render(request, 'display/doubles.html', {'lastDate': line, 'players': players.objects.filter(name__contains=" and ").order_by('-elo')})

def players_page(request):
	#get latest time
	line = str(tournaments.objects.latest(field_name='date_started').date_started)
	line = line[:10]

	#get only the latest date, not date and time
	return render(request, 'display/players.html', {'lastDate': line, 'players': players.objects.all().order_by('-elo')})

def player_detail(request):
	return HttpResponse('Constrct')

def tournament_page(request):
	#get latest time
	line = str(tournaments.objects.latest(field_name='date_started').date_started)
	line = line[:10]

	#get only the latest date, not date and time
	return render(request, 'display/tournaments.html', {'lastDate': line, 'tournaments': tournaments.objects.all().order_by('date_started')})
