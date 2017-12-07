from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse
from datetime import datetime

from . import models
from .models import *

# Create your views here.
def index(request):
	return render(request, 'display/index.html')

def rankings(request):
	#get latest time
	line = tournaments.objects.latest(field_name='date_started').getDate()

	#get only the latest date, not date and time

	return render(request, 'display/rankings.html', {'lastDate': line, 'players': players.objects.exclude(name__icontains=" and ").order_by('-elo')})
def doubles_rankings(request):
	#get latest time
	line = tournaments.objects.latest(field_name='date_started').getDate()

	#get only the latest date, not date and time
	return render(request, 'display/doubles.html', {'lastDate': line, 'players': players.objects.filter(name__contains=" and ").order_by('-elo')})

def players_page(request):
	line = tournaments.objects.latest(field_name='date_started').getDate()

	#get only the latest date, not date and time
	return render(request, 'display/players.html', {'lastDate': line, 'players': players.objects.all().order_by('name')})

def player_detail(request, player_id):
	player = get_object_or_404(players, pk=player_id)
	return render(request, 'display/player_detail.html', {'player': player})

def tournament_page(request):
	#get latest time
	line = tournaments.objects.latest(field_name='date_started').getDate()

	#this should be a table TODO TODO ADD STYLE
	#every table row should have a drop down
	#these drop downs should give the final placings for that tournament

	#get only the latest date, not date and time
	return render(request, 'display/tournaments.html', {'lastDate': line, 'tournaments': tournaments.objects.all().order_by('date_started')})
