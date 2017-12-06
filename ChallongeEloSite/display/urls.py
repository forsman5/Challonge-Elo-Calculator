from django.urls import path

from . import views

urlpatterns = [
    #ex /display/
    path('', views.index, name='index'),
    path('index.html', views.index, name='index'),
    path('rankings.html', views.rankings, name='rankings'),
    path('doubles.html', views.doubles_rankings, name='doubles'),
]
