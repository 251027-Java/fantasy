/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all lint/suspicious/noExplicitAny: any is fine */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { StatsResponse } from '../interface/StatsResponse';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  //Inject HttpClient so we can make HTTP requests
  constructor(private http:HttpClient) {}

  luckStatsResponse: StatsResponse = {stats:[]};

  currentLeagueId: string = "1252005113573150720";
  currentLeagueName: string = "Rice League";

  getLeagueLuckStats():void{
    let resp: Observable<StatsResponse> = this.http.get<StatsResponse>(`api/league/${this.currentLeagueId}/stats`)
    resp = resp.pipe(
      map<any, StatsResponse>(data => {console.log("hi");return {stats:[]}/*return JSON.parse(data)*/}/*({stats:[
        {
          name:"g",
          scores:{
            totalLuck: 1,
            medLuck: number,
            apLuck: number,
            apWins: number,
            apLoses: number,
            apTies: number,
            wins: number,
            loses: number,
            ties: number
          }
        }
      ]})*/)
    )

    //console.log(resp)
    
    resp.subscribe(data => {
      console.log(data)
      //this.luckStatsResponse.stats = data.stats;
    })

  }

}
