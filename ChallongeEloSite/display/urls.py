from django.urls import path

from . import views

urlpatterns = [
    #ex /display/
    path('', views.index, name='index'),
    path('index.html', views.index, name='index'),
    path('rankings.html', views.rankings, name='rankings'),
    path('doubles.html', views.doubles_rankings, name='doubles'),
    path('players.html', views.players_page, name='players_page'),
    path('players/<int:player_id>/', views.player_detail, name='player_detail'),
    path('tournaments.html', views.tournament_page, name='tournaments'),
]
